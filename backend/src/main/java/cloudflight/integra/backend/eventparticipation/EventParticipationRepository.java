package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class EventParticipationRepository {
    private final Map<Long, EventParticipation> eventParticipations = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<EventParticipation> findAll() {
        return new ArrayList<>(eventParticipations.values());
    }

    public Optional<EventParticipation> findById(Long id) {
        return Optional.ofNullable(eventParticipations.get(id));
    }

    public List<Long> findByEventId(Long eventId) {
        return eventParticipations.values()
            .stream()
            .filter(eventParticipation ->
                Objects.equals(eventParticipation.getEventId(), eventId)
            )
            .map(EventParticipation::getUserId)
            .toList();
    }

    public List<EventParticipation> findByUserId(Long userId) {
        return eventParticipations.values()
            .stream()
            .filter(eventParticipation ->
                Objects.equals(eventParticipation.getUserId(), userId)
            )
            .toList();
    }

    public Optional<EventParticipation> findByEventIdAndUserId(Long eventId, Long userId) {
        return eventParticipations.values()
            .stream()
            .filter(eventParticipation ->
                Objects.equals(eventParticipation.getEventId(), eventId)
                    && Objects.equals(eventParticipation.getUserId(), userId))
            .findFirst();
    }

    public boolean existsByEventIdAndUserId(Long eventId, Long userId) {
        return findByEventIdAndUserId(eventId, userId).isPresent();
    }

    public long countByEventId(Long eventId) {
        return eventParticipations.values()
            .stream()
            .filter(eventParticipation ->
                Objects.equals(eventParticipation.getEventId(), eventId))
            .count();
    }

    public EventParticipation save(EventParticipation eventParticipation) {
        if (eventParticipation.getId() == null) {
            eventParticipation.setId(idGen.getAndIncrement());
        }
        eventParticipations.put(eventParticipation.getId(), eventParticipation);
        return eventParticipation;
    }

    public void deleteById(Long id) {
        eventParticipations.remove(id);
    }

    public boolean deleteByEventIdAndUserId(Long eventId, Long userId
    ) {
        Optional<EventParticipation> eventParticipation = findByEventIdAndUserId(eventId, userId);

        if (eventParticipation.isEmpty())
            return false;

        eventParticipations.remove(eventParticipation.get().getId());
        return true;

    }

}

