package cloudflight.integra.backend.matrix;

import cloudflight.integra.backend.appevents.EventCreatedEvent;
import cloudflight.integra.backend.appevents.EventJoinedEvent;
import cloudflight.integra.backend.appevents.EventLeftEvent;
import cloudflight.integra.backend.appevents.LoginEvent;
import cloudflight.integra.backend.event.EventRepository;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.matrix.api.*;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

@Service
public class MatrixService {

    private static final Logger logger = LogManager.getLogger();

    private final RestClient client;
    private final RestClient adminClient;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final String secret;

    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private String getLocalpart(User user) {
        return user.getEmail().replace("@", "-at-");
    }

    public MatrixService(
        UserRepository userRepository,
        EventRepository eventRepository,
        @Value("${synapse.secret}") String secret,
        @Qualifier("matrixRestClient") RestClient client,
        @Qualifier("matrixAdminRestClient") RestClient adminClient
    ) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.client = client;
        this.adminClient = adminClient;
        this.secret = secret;
    }

    private String getRegisterHmac(
        String nonce,
        String username,
        String password,
        String admin
    ) throws IllegalStateException {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.secret.getBytes(StandardCharsets.UTF_8),
                HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            mac.update(nonce.getBytes(StandardCharsets.UTF_8));
            mac.update(HexFormat.of().parseHex("00"));
            mac.update(username.getBytes(StandardCharsets.UTF_8));
            mac.update(HexFormat.of().parseHex("00"));
            mac.update(password.getBytes(StandardCharsets.UTF_8));
            mac.update(HexFormat.of().parseHex("00"));
            byte[] result = mac.doFinal(admin.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();

            for (byte b : result) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to compute HMAC for Matrix registration", e);
        }
    }

    public void registerAccount(User user, boolean admin) {
        try {
            MatrixNonceResponse mxNonce = client.get()
                .uri("/_synapse/admin/v1/register")
                .retrieve().body(MatrixNonceResponse.class);
            if (mxNonce == null) return;

            String nonce = mxNonce.nonce();
            String username = getLocalpart(user);
            String password = RandomStringUtils.secure().nextAlphanumeric(8);
            String mac = getRegisterHmac(nonce, username, password, admin ? "admin" : "notadmin");

            MatrixRegisterUserRequest registerRequest = new MatrixRegisterUserRequest(
                nonce,
                username,
                password,
                admin,
                mac
            );

            MatrixRegisterUserResponse registerResponse = client.post()
                .uri("/_synapse/admin/v1/register")
                .body(registerRequest)
                .retrieve()
                .body(MatrixRegisterUserResponse.class);
            if (registerResponse == null) return;

            logger.debug("Created matrix user {}", registerResponse.user_id());
            userRepository.save(user.setMxId(registerResponse.user_id())
                .setMxPassword(Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8)))
            );

        } catch (RestClientResponseException e) {
            MatrixErrorResponse resp = e.getResponseBodyAs(MatrixErrorResponse.class);
            if (resp != null) logger.warn("{}: {}", resp.errcode(), resp.error());
        }
    }

    public void registerAccount(User user) {
        registerAccount(user, false);
    }


    public String createRoom(String roomName) {
        MatrixCreateRoomRequest request = new MatrixCreateRoomRequest(
            new CreationContent(false),
            roomName,
            "private_chat"
        );

        return Objects.requireNonNull(
            adminClient.post()
                .uri("/_matrix/client/v3/createRoom")
                .body(request)
                .retrieve()
                .body(MatrixCreateRoomResponse.class)
        ).roomId();
    }

    public void addUserToRoom(String roomId, String userId) {
        MatrixInviteRequest request = new MatrixInviteRequest(userId);
        try {
            adminClient.post()
                .uri("/_synapse/admin/v1/join/{roomId}", roomId)
                .body(request)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.Forbidden e) {
            if (e.getResponseBodyAsString().contains("already in the room")) {
                logger.debug("User {} is already in room {}, skipping join", userId, roomId);
                return;
            }
            throw e;
        }
    }

    public void removeUserFromRoom(String roomId, String userId) {
        try {

            MatrixKickRequest kickRequest = new MatrixKickRequest(userId, null);

            adminClient.post()
                .uri("/_matrix/client/v3/rooms/{roomId}/kick", roomId)
                .body(kickRequest)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException.Forbidden e) {
            if (e.getResponseBodyAsString().contains("not in room")
                || e.getResponseBodyAsString().contains("not a member")) {
                logger.debug("User {} is not in room {}, nothing to remove", userId, roomId);
                return;
            }
            throw e;
        }
    }

    @EventListener(LoginEvent.class)
    @Async
    public void provisionAccount(LoginEvent event) {
        if(event.getUser().getMxId() == null) {
            registerAccount(event.getUser());
        } else {
            logger.info("User already has a matrix account: {}, skipping creation", event.getUser().getMxId());
        }
    }

    @EventListener(EventCreatedEvent.class)
    @Async
    public void provisionEventRoom(EventCreatedEvent event) {
        Event roomEvent = event.getEvent();
        User roomUser = event.getCreator();

        String roomId = createRoom(roomEvent.getTitle());
        addUserToRoom(roomId, roomUser.getMxId());

        roomEvent.setMatrixRoomId(roomId);
        roomEvent = eventRepository.save(roomEvent);

        logger.info("Created matrix room {} for event {}", roomId, roomEvent.getId());
    }


    @EventListener(EventJoinedEvent.class)
    @Async
    public void addToMatrixRoomIfPossible(EventJoinedEvent eventJoinedEvent) {
        Event event = eventJoinedEvent.getEvent();
        User user = eventJoinedEvent.getUser();
        if (event.getMatrixRoomId() == null || user.getMxId() == null) return;
        try {
            addUserToRoom(event.getMatrixRoomId(), user.getMxId());
            logger.info("Added user {} to Matrix room {} of event {}",
                user.getMxId(), event.getMatrixRoomId(), event.getId());
        } catch (Exception e) {
            logger.warn("Failed to add user {} to Matrix room {} for event {}",
                user.getMxId(), event.getMatrixRoomId(), event.getId(), e);
        }
    }


    @EventListener(EventLeftEvent.class)
    @Async
    public void removeFromMatrixRoomIfPossible(EventLeftEvent eventLeftEvent) {
        Event event = eventLeftEvent.getEvent();
        User user = eventLeftEvent.getUser();
        if (event.getMatrixRoomId() == null || user.getMxId() == null) return;
        try {
            removeUserFromRoom(event.getMatrixRoomId(), user.getMxId());
            logger.info("Removed user {} from Matrix room {} of event {}",
                user.getMxId(), event.getMatrixRoomId(), event.getId());
        } catch (Exception e) {
            logger.warn("Failed to remove user {} from Matrix room {} for event {}",
                user.getMxId(), event.getMatrixRoomId(), event.getId(), e);
        }
    }

}
