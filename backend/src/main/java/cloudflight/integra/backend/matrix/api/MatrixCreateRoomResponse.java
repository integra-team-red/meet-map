package cloudflight.integra.backend.matrix.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixCreateRoomResponse(
    @JsonProperty("room_id")
    String roomId
) {
}
