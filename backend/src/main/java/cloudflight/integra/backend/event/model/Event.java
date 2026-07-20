package cloudflight.integra.backend.event.model;
import cloudflight.integra.backend.tag.model.Tag;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "address")
    private String address;
    @Column(name = "city")
    private String city;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "max_participants")
    private Integer maxParticipants;
    @Column(name = "min_age")
    private Integer minAge;
    @Column(name = "max_age")
    private Integer maxAge;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;
    @Column(name = "creator_id")
    private Long creatorId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    (
        name = "event_tags",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    public Event() { }

    public Event(
        Long id,
        String title,
        String description,
        String address,
        String city,
        Double latitude,
        Double longitude,
        LocalDateTime dateTime,
        Integer maxParticipants,
        Integer minAge,
        Integer maxAge,
        EventStatus status,
        Long creatorId,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.address = address;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateTime = dateTime;
        this.maxParticipants = maxParticipants;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.status = status;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
    }

    @PrePersist
    void onCreate(){
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EventStatus.ACTIVE;
        }
    }

    public Long getId() {
        return id;
    }

    public Event setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Event setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Event setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getCity() {
        return city;
    }

    public Event setCity(String city) {
        this.city = city;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Event setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Event setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Event setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public Event setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
        return this;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public Event setMinAge(Integer minAge) {
        this.minAge = minAge;
        return this;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public Event setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public EventStatus getStatus() {
        return status;
    }

    public Event setStatus(EventStatus status) {
        this.status = status;
        return this;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public Event setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Event setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public Event setTags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }
}
