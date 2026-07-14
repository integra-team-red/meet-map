package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EventRepository {
    private final Map<Long, Event> events = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Event> findAll() {
        return new ArrayList<>(events.values());
    }

    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(events.get(id));
    }

    public Event save(Event event) {
        if (event.getId() == null) {
            event.setId(idGen.getAndIncrement());
            event.setCreatedAt(LocalDateTime.now());
            if (event.getStatus() == null) {
                event.setStatus(EventStatus.ACTIVE);
            }
        }
        events.put(event.getId(), event);
        return event;
    }

    public boolean deleteById(Long id) {
        Event event = events.get(id);
        if (event != null && event.getStatus() != EventStatus.CANCELLED) {
            event.setStatus(EventStatus.CANCELLED);
            return true;
        }
        return false;
    }
}
