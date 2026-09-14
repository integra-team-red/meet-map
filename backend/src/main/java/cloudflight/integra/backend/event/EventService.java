package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.matrix.model.api.MatrixRoomCreationRestClientService;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
    private final MatrixRoomCreationRestClientService matrixRoomCreationRestClientService;

    private static final Logger logger = LogManager.getLogger();

    public EventService(
        EventRepository repository,
        UserRepository userRepository,
        MatrixRoomCreationRestClientService matrixRoomCreationRestClientService
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.matrixRoomCreationRestClientService = matrixRoomCreationRestClientService;
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
        EventStatus status
    ) {
        boolean noTags = tagIds == null || tagIds.isEmpty();
        List<Long> tags = noTags ? List.of(-1L) : tagIds;
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);
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
