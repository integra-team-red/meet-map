package cloudflight.integra.backend.auth.model;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequest(
    @NotBlank(message = "A first name is required.")
    @Size(max = 100, message = "The first name is too long")
    String firstName,
    @NotBlank(message = "A last name is required.")
    @Size(max = 100, message = "The last name is too long")
    String lastName,
    @NotBlank(message = "An email is required.")
    @Email(message = "Must provide a valid email address.")
    @Size(max = 255, message = "The email is too long.")
    String email,
    @NotBlank(message = "A password is required.")
    @Size(min = 8, message = "The password must be at least 8 characters long.")
    String password,
    @NotNull
    @Past(message = "The birth date must be in the past.")
    LocalDate birthDate,
    @NotNull
    @Size(max = 500, message = "The description is too long.")
    String description
) {
}
