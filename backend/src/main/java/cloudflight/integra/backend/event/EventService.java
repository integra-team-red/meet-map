package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
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
        Long creatorId
    ) {
        boolean noTags = tagIds == null || tagIds.isEmpty();
        List<Long> tags = noTags ? List.of(-1L) : tagIds;
        LocalDateTime from = dateFrom.atStartOfDay();
        LocalDateTime to = dateTo.atTime(LocalTime.MAX);
        return repository.findFiltered(searchTerm, city, tags, noTags, minAge, maxAge, from, to, creatorId, pageable);
    }

    public Optional<Event> getById(Long id) {
        return repository.findById(id);
    }

    public Event create(Event event) {
        event.setId(null);
        event.setCreatedAt(LocalDateTime.now());
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.ACTIVE);
        }
        return repository.save(event);
    }

    public Optional<Event> update(Long id, Event event) {
        return repository.findById(id).map(existing -> {
            event.setId(id);
            event.setCreatedAt(existing.getCreatedAt());
            event.setCreatorId(existing.getCreatorId());
            if (event.getStatus() == null) {
                event.setStatus(existing.getStatus());
            }
            return repository.save(event);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existing -> {
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
