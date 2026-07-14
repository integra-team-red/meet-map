package cloudflight.integra.backend.event.model;
import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String title;
    private String description;
    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private LocalDateTime dateTime;
    private Integer maxParticipants;
    private Integer minAge;
    private Integer maxAge;
    private EventStatus status;
    private Long creatorId; // passed as request parameter for now
    private LocalDateTime createdAt; // auto-set on creation

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
}
