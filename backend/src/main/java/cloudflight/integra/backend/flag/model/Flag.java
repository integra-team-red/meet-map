package cloudflight.integra.backend.flag.model;


import java.time.LocalDateTime;

public class Flag {
    private Long id;
    private Long userId;
    private Long eventId;
    private String reason;
    private LocalDateTime createdAt;


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Flag setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getReason() {
        return reason;
    }

    public Flag setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public Long getEventId() {
        return eventId;
    }

    public Flag setEventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public Flag setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Long getId() {
        return id;
    }

    public Flag setId(Long id) {
        this.id = id;
        return this;
    }

}
