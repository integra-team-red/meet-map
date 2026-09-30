package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.geocoding.GeocodingService;
import cloudflight.integra.backend.geocoding.model.Coordinate;
import cloudflight.integra.backend.matrix.model.api.MatrixRoomCreationRestClientService;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    private final EventRepository repository;
    private final UserRepository userRepository;
    private final MatrixRoomCreationRestClientService matrixRoomCreationRestClientService;
    private final GeocodingService geocodingService;
    private final EventImageService imageService;
    private static final Logger logger = LogManager.getLogger();

    public EventService(
        EventRepository repository,
        UserRepository userRepository,
        MatrixRoomCreationRestClientService matrixRoomCreationRestClientService,
        GeocodingService geocodingService,
        EventImageService imageService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.matrixRoomCreationRestClientService = matrixRoomCreationRestClientService;
        this.geocodingService = geocodingService;
        this.imageService = imageService;
    }

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

            String roomId = matrixRoomCreationRestClientService.createRoom(savedEvent.getTitle());

            matrixRoomCreationRestClientService.addUserToRoom(roomId, user.getMxId());

            savedEvent.setMatrixRoomId(roomId);
            savedEvent = repository.save(savedEvent);

            logger.debug(
                "Created matrix room {} for event {}",
                roomId,
                savedEvent.getId()
            );
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
        User user = findUser(email);
        Optional<Event> found = repository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        Event existing = found.get();
        checkCanModify(existing, user);
        event.setId(id);
        event.setCreatedAt(existing.getCreatedAt());
        event.setCreatorId(existing.getCreatorId());
        event.setImageId(existing.getImageId());
        event.setMatrixRoomId(existing.getMatrixRoomId());
        if (event.getStatus() == null) {
            event.setStatus(existing.getStatus());
        }
        return Optional.of(repository.save(event));
    }

    public boolean delete(Long id, String email) {
        User user = findUser(email);
        Optional<Event> found = repository.findById(id);
        if (found.isEmpty()) {
            return false;
        }

        Event existing = found.get();
        checkCanModify(existing, user);
        existing.setStatus(EventStatus.CANCELLED);
        repository.save(existing);
        return true;
    }

    public List<String> getCities() {
        return repository.findDistinctCities();
    }

    public Page<Event> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<Event> attachImage(Long id, MultipartFile file, String email) {
        User user = findUser(email);
        Optional<Event> found = repository.findById(id);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        Event existing = found.get();
        checkCanModify(existing, user);

        UUID oldImageId = existing.getImageId();
        existing.setImageId(imageService.save(file));
        Event saved = repository.save(existing);

        if (oldImageId != null) {
            imageService.delete(oldImageId);
        }
        return Optional.of(saved);
    }


    private User findUser(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found."));
    }

    private void checkCanModify(Event event, User user) {
        boolean isCreator = event.getCreatorId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;
        if (!isCreator && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to take this action.");
        }
    }

}
