package cloudflight.integra.backend.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateRoomRequest(
    @JsonProperty("creation_content")
    RoomCreationContent creationContent,
    String name,
    String preset
) {
}
