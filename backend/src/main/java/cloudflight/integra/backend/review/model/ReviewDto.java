package cloudflight.integra.backend.review.model;

import cloudflight.integra.backend.event.model.EventDto;

import java.time.LocalDateTime;

public record ReviewDto(Long id, Long userId, EventDto event, Integer rating, String comment, LocalDateTime createdAt) {
}
