package cloudflight.integra.backend.event.model;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateEventDto(
    @NotBlank(message = "The event must have a title.")
    @Size(max = 255, message = "The title is too long.")
    String title,
    @NotBlank(message = "Must provide a description for an event.")
    String description,
    @NotBlank(message = "The event must have an address.")
    String address,
    @NotBlank(message = "The event must happen in a city.")
    String city,
    @Min(value = -90, message = "Invalid latitude.")
    @Max(value = 90, message = "Invalid latitude.")
    Double latitude,
    @Min(value = -180, message = "Invalid longitude.")
    @Max(value = 180, message = "Invalid longitude.")
    Double longitude,
    @NotNull(message = "The event must have a date.")
    @Future(message = "An event must be set in the future.")
    LocalDateTime dateTime,
    @Min(value = 1, message = "The capacity must allow at least one person.")
    Integer maxParticipants,
    @Min(value = 0, message = "The age cannot be negative.")
    Integer minAge,
    @Min(value = 0, message = "The age cannot be negative.")
    Integer maxAge,
    @NotNull(message = "Must provide a creator.")
    Long creatorId,
    Set<Long> tagIds
) {
    @AssertTrue(message = "The minimum age must be less than the maximum age.")
    private boolean isAgeRangeValid() {
        return minAge == null || maxAge == null || minAge <= maxAge;
    }
}
