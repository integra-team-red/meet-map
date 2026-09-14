package cloudflight.integra.backend.matrix.model.api;

import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class MatrixChatService {

    private final RestClient matrixClient;
    private final UserRepository userRepository;

    public MatrixChatService(@Qualifier("restClient") RestClient matrixClient,
                             UserRepository userRepository) {
        this.matrixClient = matrixClient;
        this.userRepository = userRepository;
    }

    public List<MatrixMessage> getMessage(String userEmail, String roomId) {
        String accessToken = login(userEmail);

        MatrixMessagesResponse response = matrixClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/_matrix/client/v3/rooms/{roomId}/messages")
                .queryParam("dir", "b")
                .queryParam("limit",50)
                .build(roomId))
            .header("Authorization","Bearer " + accessToken)
            .retrieve()
            .body(MatrixMessagesResponse.class);

        if (response == null || response.chunk() == null) {
            return List.of();
        }

        return response.chunk().stream()
            .filter(message -> "m.room.message".equals(message.type()))
            .filter(message -> message.content() != null)
            .toList();
    }
    public void sendMessage(String userEmail, String roomId, String message) {
        String accessToken = login(userEmail);

        MatrixSendMessageRequest request =
            new MatrixSendMessageRequest(
                "m.text",
                message
            );

        matrixClient.put()
            .uri(
                "/_matrix/client/v3/rooms/{roomId}/send/m.room.message/{transactionId}",
                roomId,
                UUID.randomUUID().toString()
            )
            .header("Authorization", "Bearer " + accessToken)
            .body(request)
            .retrieve()
            .toBodilessEntity();
    }
    private String login(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getMxId() == null || user.getMxPassword() == null) {
            throw new IllegalStateException("User does not have a Matrix account");
        }

        String password = new String(
            Base64.getDecoder().decode(user.getMxPassword()),
            StandardCharsets.UTF_8
        );

        MatrixLoginRequest request = new MatrixLoginRequest(
            "m.login.password",
            new MatrixIdentifier("m.id.user", user.getMxId()),
            password
        );

        MatrixLoginResponse response = matrixClient.post()
            .uri("/_matrix/client/v3/login")
            .body(request)
            .retrieve()
            .body(MatrixLoginResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("Matrix login failed");
        }

        return response.accessToken();
    }

    public record MatrixLoginRequest(
        String type,
        @JsonProperty("identifier")
        MatrixIdentifier identifier,
        String password
    ) {}

    public record MatrixIdentifier(
        String type,
        String user
    ) {}

    public record MatrixLoginResponse(
        @JsonProperty("access_token")
        String accessToken
    ) {}

    public record MatrixSendMessageRequest(
        @JsonProperty("msgtype")
        String msgType,
        String body
    ) {}

    public record MatrixMessagesResponse(
        List<MatrixMessage> chunk
    ) {}

    public record MatrixMessage(
        @JsonProperty("event_id")
        String eventId,

        String type,

        @JsonProperty("sender")
        String sender,

        MatrixMessageContent content
    ) {}

    public record MatrixMessageContent(
        @JsonProperty("msgtype")
        String msgType,

        String body
    ) {}


}
