package cloudflight.integra.backend.user;

import cloudflight.integra.backend.event.EventRepository;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.eventparticipation.EventParticipationRepository;
import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.flag.FlagRepository;
import cloudflight.integra.backend.matrix.model.api.MatrixRoomCreationRestClientService;
import cloudflight.integra.backend.review.ReviewRepository;
import cloudflight.integra.backend.tag.TagRepository;
import cloudflight.integra.backend.tag.model.Tag;
import cloudflight.integra.backend.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repository;
    private final TagRepository tagRepository;
    private final EventRepository eventRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final ReviewRepository reviewRepository;
    private final FlagRepository flagRepository;
    private final MatrixRoomCreationRestClientService matrixRoomCreationRestClientService;

    public UserService(
        UserRepository repository, TagRepository tagRepository, EventRepository eventRepository,
        EventParticipationRepository eventParticipationRepository,
        ReviewRepository reviewRepository,
        FlagRepository flagRepository,
        MatrixRoomCreationRestClientService matrixRoomCreationRestClientService
    ) {
        this.repository = repository;
        this.tagRepository = tagRepository;
        this.eventRepository = eventRepository;
        this.eventParticipationRepository = eventParticipationRepository;
        this.reviewRepository = reviewRepository;
        this.flagRepository = flagRepository;
        this.matrixRoomCreationRestClientService = matrixRoomCreationRestClientService;
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email).map((user) -> {
            if (user.getMxPassword() != null)
                user.setMxPassword(
                    new String(Base64.getDecoder().decode(user.getMxPassword()), StandardCharsets.UTF_8)
                );
            return user;
        }).orElseThrow(() -> new
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public Set<Tag> getTags(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
            .getTags();
    }

    public User updateTags(String email, Set<Long> tagIds) {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Tag> tagList = tagRepository.findAllById(tagIds);
        if (tagIds.size() != tagList.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more tags not found");
        }
        Set<Tag> newTags = new HashSet<>(tagList);
        user.setTags(newTags);
        return repository.save(user);
    }

    @Transactional
    public void deleteProfile(String email) {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Event> hosted = eventRepository.findByCreatorIdAndStatus(user.getId(), EventStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        boolean hostsSoonEvent = hosted.stream().anyMatch(event -> event.getDateTime() != null
            && event.getDateTime().isAfter(now)
            && event.getDateTime().isBefore(now.plusHours(24)));
        if (hostsSoonEvent) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "You cannot delete your account while hosting an event in less than 24 hours.");
        }

        for (Event event : hosted) {
            event.setStatus(EventStatus.CANCELLED);
        }
        eventRepository.saveAll(hosted);
        List<EventParticipation> participations = eventParticipationRepository.findByUserId(user.getId());
        for (EventParticipation participation : participations) {
            leaveRoom(participation.getEvent(), user);
        }
        eventParticipationRepository.deleteAll(participations);

        reviewRepository.deleteByUserId(user.getId());
        flagRepository.deleteByUserId(user.getId());

        deactivateMatrix(user);
        repository.delete(user);
    }

    private void leaveRoom(Event event, User user) {
        if (event.getMatrixRoomId() == null || user.getMxId() == null) return;
        try {
            matrixRoomCreationRestClientService.removeUserFromRoom(event.getMatrixRoomId(), user.getMxId());
        } catch (RestClientException _) {
        }
    }

    private void deactivateMatrix(User user) {
        if (user.getMxId() == null) return;
        try {
            matrixRoomCreationRestClientService.deactivateAccount(user.getMxId());
        } catch (RestClientException _) {

        }
    }
}
