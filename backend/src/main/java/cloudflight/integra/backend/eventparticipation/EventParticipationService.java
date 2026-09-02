package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.event.EventRepository;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class EventParticipationService {
    private final EventParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public EventParticipationService(
        EventParticipationRepository participationRepository,
        UserRepository userRepository,
        EventRepository eventRepository
    ) {

        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    public List<EventParticipation> getAll() {
        return participationRepository.findAll();
    }

    public Optional<EventParticipation> getById(Long id) {
        return participationRepository.findById(id);
    }

    public synchronized EventParticipation joinEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        boolean alreadyJoined = participationRepository.existsByEventIdAndUserId(eventId, user.getId());

        if (alreadyJoined) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already joined this event");
        }

        if (event.getMaxParticipants() != null) {
            long currentCount = participationRepository.countByEventId(eventId);

            if (currentCount >= event.getMaxParticipants()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is full");
            }
        }

        if (user.getBirthDate() != null && event.getDateTime() != null) {
            int ageAtEvent = Period.between(
                user.getBirthDate(),
                event.getDateTime().toLocalDate()
            ).getYears();

            if (event.getMinAge() != null && ageAtEvent < event.getMinAge()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You must be at least " + event.getMinAge() + " years old to join this event");
            }
        }
        EventParticipation participation = new EventParticipation();
        participation.setUserId(user.getId());
        participation.setEvent(event);
        participation.setJoinedAt(LocalDateTime.now());

        return participationRepository.save(participation);

    }

    public Page<EventParticipation> getParticipants(Long eventId, Pageable pageable) {
        return participationRepository.findByEventId(eventId, pageable);
    }

    public Page<Event> getEventsByParticipant(Long userId, Pageable pageable) {
        return participationRepository.findAllEventsByParticipant(userId, pageable);
    }

    public void leaveEvent(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        EventParticipation participation = participationRepository
            .findByEventIdAndUserId(eventId, user.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found"));

        participationRepository.deleteById(participation.getId());
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

    public Optional<EventParticipation> getLastPendingReview(Long userId) {
        List<EventParticipation> results = participationRepository.findPendingReviews(userId, PageRequest.of(0, 1));

        if (results.isEmpty())
            return Optional.empty();

        return Optional.of(results.getFirst());
    }

    public void dismissReview(Long eventId, Long userId) {
        EventParticipation participation = participationRepository.findByEventIdAndUserId(eventId, userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found"));

        participation.setReviewDismissed(true);
        participationRepository.save(participation);
    }
}
