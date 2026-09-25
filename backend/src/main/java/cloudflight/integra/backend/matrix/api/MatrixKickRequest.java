package cloudflight.integra.backend.matrix.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatrixKickRequest (
    @JsonProperty("user_id")
    String userId,
    String reason
) {

}
