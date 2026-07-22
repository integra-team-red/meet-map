package cloudflight.integra.backend.flag.model;


import cloudflight.integra.backend.event.model.Event;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    public Event getEvent() {
        return event;
    }

    public Flag setEvent(Event event) {
        this.event = event;
        return this;
    }

    @PrePersist
    void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
