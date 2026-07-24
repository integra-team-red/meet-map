package cloudflight.integra.backend.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "An email is required.")
    @Email(message = "Must provide a valid email address.")
    @Size(max = 255, message = "The email is too long.")
    String email,
    @NotBlank(message = "A password is required.")
    @Size(min = 8, message = "The password must be at least 8 characters long.")
    String password
) {
}
