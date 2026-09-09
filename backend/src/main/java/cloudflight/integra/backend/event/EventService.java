package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.Role;
import cloudflight.integra.backend.user.model.User;
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

    public EventService(EventRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
        Double latitude
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
        return repository.save(event);
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
