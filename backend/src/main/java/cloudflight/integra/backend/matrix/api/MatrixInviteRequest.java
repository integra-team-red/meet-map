package cloudflight.integra.backend.matrix.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixInviteRequest(
    @JsonProperty("user_id")
    String userId
) {
}
