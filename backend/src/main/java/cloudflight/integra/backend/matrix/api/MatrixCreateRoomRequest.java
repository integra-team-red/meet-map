package cloudflight.integra.backend.matrix.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixCreateRoomRequest(
    @JsonProperty("creation_content")
    CreationContent creationContent,
    String name,
    String preset
) {
}
