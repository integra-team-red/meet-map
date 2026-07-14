package cloudflight.integra.backend.eventparticipation.model;

import java.time.LocalDateTime;

public class EventParticipation {
    private Long id;
    private Long userId;
    private Long eventId;
    private LocalDateTime joinedAt;

    public EventParticipation() {
    }

    public EventParticipation(Long id, Long userId, Long eventId, LocalDateTime joinedAt) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.joinedAt = joinedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

}
