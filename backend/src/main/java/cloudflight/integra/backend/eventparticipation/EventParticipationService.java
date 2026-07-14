package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventParticipationService {
    private final EventParticipationRepository participationRepository;

    public EventParticipationService(EventParticipationRepository participationRepository) {
        this.participationRepository = participationRepository;
    }

    public List<EventParticipation> getAll() {
        return participationRepository.findAll();
    }

    public Optional<EventParticipation> getById(Long id) {
        return participationRepository.findById(id);
    }

    public synchronized EventParticipation joinEvent(Long eventId, Long userId) {

        boolean alreadyJoined = participationRepository.existsByEventIdAndUserId(eventId, userId);

        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already joined this event");
        }

        long currentParticipationCount = participationRepository.countByEventId(eventId);
        //TODO: check EventParticipation count


        EventParticipation eventParticipation = new EventParticipation(null, userId, eventId, LocalDateTime.now());

        return participationRepository.save(eventParticipation);
    }

    public List<Long> getParticipants(Long eventId) {
        return participationRepository.findByEventId(eventId);
    }

    public void leaveEvent(Long eventId, Long userId) {
        participationRepository.deleteByEventIdAndUserId(eventId, userId);

    }

    public EventParticipation create(EventParticipation eventParticipation) {
        return participationRepository.save(eventParticipation);
    }

    public Optional<EventParticipation> update(Long id, EventParticipation eventParticipation) {
        return participationRepository.findById(id).map(_ -> {
            eventParticipation.setId(id);
            return participationRepository.save(eventParticipation);
        });
    }

    public boolean delete(Long id) {
        return participationRepository.findById(id).map(_ -> {
            participationRepository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
