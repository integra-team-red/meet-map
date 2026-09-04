package cloudflight.integra.backend.matrix;

import cloudflight.integra.backend.matrix.model.*;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
public class MatrixService {

    private static final Logger logger = LogManager.getLogger();

    private final RestClient matrixClient;
    private final UserRepository userRepository;
    private final String secret;

    private static final String HMAC_ALGORITHM = "HmacSHA1";

    private String getLocalpart(User user) {
        return user.getEmail().replace("@", "-at-");
    }

    public MatrixService(
        UserRepository userRepository,
        @Value("${synapse.url}") String url,
        @Value("${synapse.secret}") String secret
    ) {
        this.userRepository = userRepository;
        this.matrixClient = RestClient.create(url);
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
            MatrixNonce mxNonce = matrixClient.get()
                .uri("/_synapse/admin/v1/register")
                .retrieve().body(MatrixNonce.class);
            if (mxNonce == null) return;

            String nonce = mxNonce.nonce();
            String username = getLocalpart(user);
            String password = RandomStringUtils.secure().nextAlphanumeric(8);
            String mac = getRegisterHmac(nonce, username, password, admin ? "admin" : "notadmin");

            MatrixRegisterRequest registerRequest = new MatrixRegisterRequest(nonce, username, password, admin, mac);

            MatrixRegisterResponse registerResponse = matrixClient.post()
                .uri("/_synapse/admin/v1/register")
                .body(registerRequest)
                .retrieve()
                .body(MatrixRegisterResponse.class);
            if (registerResponse == null) return;

            logger.info("Created matrix user {}", registerResponse.user_id());
            userRepository.save(user.setMxId(registerResponse.user_id()).setMxPassword(password));

        } catch (RestClientResponseException e) {
            MatrixErrorResponse resp = e.getResponseBodyAs(MatrixErrorResponse.class);
            if (resp != null) logger.warn("{}: {}", resp.errcode(), resp.error());
        }
    }

    public void registerAccount(User user) {
        registerAccount(user, false);
    }

}
