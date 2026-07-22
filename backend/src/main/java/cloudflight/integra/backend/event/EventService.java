package cloudflight.integra.backend.event;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    // TODO: might want to filter out the soft deleted events in the future
    public List<Event> getAll() {
        return repository.findAll();
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

    // soft delete: events are cancelled, not removed, since flags hold a
    // foreign key to event and a hard delete would violate that constraint
    public boolean delete(Long id) {
        return repository.findById(id).map(existing -> {
            existing.setStatus(EventStatus.CANCELLED);
            repository.save(existing);
            return true;
        }).orElse(false);
    }
}
