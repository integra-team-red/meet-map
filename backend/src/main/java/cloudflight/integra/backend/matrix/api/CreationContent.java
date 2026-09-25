package cloudflight.integra.backend.matrix.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreationContent(
    @JsonProperty("is_direct")
    boolean isDirect
) {
}
