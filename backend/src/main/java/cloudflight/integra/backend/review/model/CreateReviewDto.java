package cloudflight.integra.backend.review.model;

import java.time.LocalDateTime;

public record CreateReviewDto(
    Long id,
    Long userId,
    Long eventId,
    Integer rating,
    String comment,
    LocalDateTime createdAt) {

}
