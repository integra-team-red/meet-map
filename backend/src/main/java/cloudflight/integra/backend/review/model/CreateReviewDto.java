package cloudflight.integra.backend.review.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewDto(
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be higher than 5")
    Integer rating,
    @Size(max = 100, message = "Message cannot be longer than 100 characters.")
    String comment) {
}
