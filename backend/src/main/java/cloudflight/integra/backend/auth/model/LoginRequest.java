package cloudflight.integra.backend.auth.model;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "An email is required.")
    String email,
    @NotBlank(message = "A password is required.")
    String password
) {
}
