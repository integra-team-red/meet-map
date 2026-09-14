package cloudflight.integra.backend.matrix.model.api;

import cloudflight.integra.backend.matrix.model.CreateRoomRequest;
import cloudflight.integra.backend.matrix.model.MatrixInviteRequest;
import cloudflight.integra.backend.matrix.model.MatrixRoomCreationResponseContent;
import cloudflight.integra.backend.matrix.model.RoomCreationContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Objects;

@Service
public class MatrixRoomCreationRestClientService {

    private static final Logger logger = LogManager.getLogger();

    private final RestClient restClient;

    public MatrixRoomCreationRestClientService(
        @Qualifier("matrixAdminRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public String createRoom(String roomName) {
        CreateRoomRequest request = new CreateRoomRequest(
            new RoomCreationContent(false),
            roomName,
            "private_chat"
        );

        return Objects.requireNonNull(
            restClient.post()
                .uri("/_matrix/client/v3/createRoom")
                .body(request)
                .retrieve()
                .body(MatrixRoomCreationResponseContent.class)
        ).roomId();
    }

    public void addUserToRoom(String roomId, String userId) {
        MatrixInviteRequest request = new MatrixInviteRequest(userId);

        try {
            restClient.post()
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
            restClient.post()
                .uri("/_synapse/admin/v1/rooms/{roomId}/make_room_admin", roomId)
                .body(new MakeRoomAdminRequest(null))
                .retrieve()
                .toBodilessEntity();

            MatrixInviteRequest kickRequest = new MatrixInviteRequest(userId);

            restClient.post()
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

    private record MakeRoomAdminRequest(String userId) {}
}
