package cloudflight.integra.backend.eventparticipation.model;

import cloudflight.integra.backend.event.model.Event;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class EventParticipation {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="event_id", nullable=false)
    private Event event;
    private LocalDateTime joinedAt;

    public EventParticipation() {
    }

    public EventParticipation(Long id, Long userId, Event event, LocalDateTime joinedAt) {
        this.id = id;
        this.userId = userId;
        this.event = event;
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) { this.event = event;}

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

}
