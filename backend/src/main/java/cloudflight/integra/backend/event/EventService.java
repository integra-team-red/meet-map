package cloudflight.integra.backend.event;

import cloudflight.integra.backend.appevents.EventCreatedEvent;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.geocoding.GeocodingService;
import cloudflight.integra.backend.geocoding.model.Coordinate;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final GeocodingService geocodingService;
    private final ApplicationEventPublisher eventPublisher;

    private static final Logger logger = LogManager.getLogger();

    public EventService(
        EventRepository repository,
        UserRepository userRepository,
        GeocodingService geocodingService,
        ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.geocodingService = geocodingService;
        this.eventPublisher = eventPublisher;
    }

    // TODO: might want to filter out the soft deleted events in the future
    public Page<Event> getAll(
        Pageable pageable,
        String searchTerm,
        String city,
        List<Long> tagIds,
        Integer minAge,
        Integer maxAge,
        LocalDate dateFrom,
        LocalDate dateTo,
        Long creatorId,
        EventStatus status,
        Double longitude,
        Double latitude,
        Double minLat,
        Double maxLat,
        Double minLon,
        Double maxLon
    ) {
        boolean noTags = tagIds == null || tagIds.isEmpty();
        List<Long> tags = noTags ? List.of(-1L) : tagIds;
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);

        if(latitude != null && longitude != null) {
            Specification<Event> spec = Specification.allOf(
                    EventRepository.search(searchTerm),
                    EventRepository.hasCity(city),
                    EventRepository.inAgeRange(minAge, maxAge),
                    EventRepository.inDateRange(from, to),
                    EventRepository.hasCreator(creatorId),
                    EventRepository.hasStatus(status),
                    EventRepository.hasTags(noTags ? List.of() : tagIds),
                    EventRepository.withinBounds(minLat, maxLat, minLon, maxLon),
                    EventRepository.orderByNearest(latitude, longitude)
                );
            return repository.findAll(
                spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()));
        }

        return repository.findFiltered(searchTerm, city, tags, noTags, minAge,
            maxAge, from, to, creatorId, status, pageable);
    }

    public Optional<Event> getById(Long id) {
        return repository.findById(id);
    }

    public Event create(Event event, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        event.setId(null);
        event.setCreatorId(user.getId());
        event.setCreatedAt(LocalDateTime.now());
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.ACTIVE);
        }
        if(event.getLatitude()==null || event.getLongitude()==null){
            Coordinate coordinate = geocodingService.getCoordinates(event.getCity(), event.getAddress());
            event.setLongitude(coordinate.getLongitude());
            event.setLatitude(coordinate.getLatitude());
        }
        Event savedEvent = repository.save(event);

        try {
            if (user.getMxId() == null) {
                throw new IllegalStateException("User does not have a Matrix account");
            }

            eventPublisher.publishEvent(new EventCreatedEvent(this, savedEvent, user));
        } catch (Exception e) {
            logger.warn(
                "Failed to create matrix room for event {}",
                savedEvent.getId(),
                e
            );
        }
        return savedEvent;
    }

    public Optional<Event> update(Long id, Event event, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
        return repository.findById(id).map(existing -> {
            if(!existing.getCreatorId().equals(user.getId()) && user.getRole() != Role.ADMIN)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to take this action.");
            event.setId(id);
            event.setCreatedAt(existing.getCreatedAt());
            event.setCreatorId(existing.getCreatorId());
            if (event.getStatus() == null) {
                event.setStatus(existing.getStatus());
            }
            return repository.save(event);
        });
    }

    public boolean delete(Long id, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return repository.findById(id).map(existing -> {
            if(!existing.getCreatorId().equals(user.getId()) && user.getRole() != Role.ADMIN)
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to take this action.");
            existing.setStatus(EventStatus.CANCELLED);
            repository.save(existing);
            return true;
        }).orElse(false);
    }

    public List<String> getCities() {
        return repository.findDistinctCities();
    }

    public Page<Event> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
