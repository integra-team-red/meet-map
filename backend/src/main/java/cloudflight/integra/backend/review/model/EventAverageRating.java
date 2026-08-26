package cloudflight.integra.backend.review.model;

public record EventAverageRating(Long eventId, Double averageRating, Long reviewCount) {
}
