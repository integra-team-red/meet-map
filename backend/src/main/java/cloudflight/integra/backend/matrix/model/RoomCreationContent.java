package cloudflight.integra.backend.matrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoomCreationContent(
    @JsonProperty("is_direct")
    boolean isDirect
) {
}
