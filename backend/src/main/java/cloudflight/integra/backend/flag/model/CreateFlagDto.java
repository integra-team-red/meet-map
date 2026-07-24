package cloudflight.integra.backend.flag.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateFlagDto(
    Long id,
    @NotNull(message = "Event id is required.")
    Long eventId,
    @NotBlank(message = "Reason is required.")
    @Size(max = 255)
    String reason) {
}
