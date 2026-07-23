package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public synchronized EventParticipation joinEvent(EventParticipation eventParticipation) {
        boolean alreadyJoined = participationRepository.existsByEventIdAndUserId(
            eventParticipation.getEvent().getId(),
            eventParticipation.getUserId()
        );

        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User has already joined this event");
        }
        eventParticipation.setJoinedAt(LocalDateTime.now());
        long currentParticipationCount = participationRepository.countByEventId(eventParticipation.getEvent().getId());
        //TODO: check EventParticipation count
        return participationRepository.save(eventParticipation);
    }

    public Page<Long> getParticipants(Long eventId, Pageable pageable) {
        return participationRepository.findAllUserIdsByEventId(eventId, pageable);
    }

    public void leaveEvent(Long id) {
        participationRepository.deleteById(id);
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
