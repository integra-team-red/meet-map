package cloudflight.integra.backend.review.model;

import java.time.LocalDateTime;

public class Review {
    private Long id;
    private Long userId;
    private Long eventId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public Review() {
    }

    public Review(Long id, Long userId, Long eventId, Integer rating, String comment, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Review setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Review setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getEventId() {
        return eventId;
    }

    public Review setEventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public Review setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public Integer getRating() {
        return rating;
    }

    public Review setRating(Integer rating) {
        this.rating = rating;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Review setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
}

