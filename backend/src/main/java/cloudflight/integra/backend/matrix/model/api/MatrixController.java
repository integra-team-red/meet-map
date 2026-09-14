package cloudflight.integra.backend.matrix.model.api;

import cloudflight.integra.backend.event.EventRepository;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.eventparticipation.EventParticipationRepository;
import cloudflight.integra.backend.user.UserRepository;
import cloudflight.integra.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/chat")
public class MatrixController {

    private final EventRepository eventRepository;
    private final EventParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final MatrixChatService matrixChatService;

    public MatrixController(
        EventRepository eventRepository,
        EventParticipationRepository participationRepository,
        UserRepository userRepository,
        MatrixChatService matrixChatService
    ) {
        this.eventRepository = eventRepository;
        this.participationRepository = participationRepository;
        this.userRepository = userRepository;
        this.matrixChatService = matrixChatService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<MatrixChatService.MatrixMessage> getMessages(
        @PathVariable Long eventId,
        Authentication authentication
    ) {
        Event event = getEvent(eventId);
        User user = getUser(authentication);

        checkAccess(event, user);

        return matrixChatService.getMessage(
            user.getEmail(),
            event.getMatrixRoomId()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void sendMessage(
        @PathVariable Long eventId,
        @RequestBody SendMessageRequest request,
        Authentication authentication
        ) {
        Event event = getEvent(eventId);
        User user = getUser(authentication);

        checkAccess(event, user);

        if (request.message() == null || request.message().isBlank()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Message cannot be empty"
            );
        }

        matrixChatService.sendMessage(
            user.getEmail(),
            event.getMatrixRoomId(),
            request.message()
        );

    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Event not found"
                )
            );
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
                )
            );
    }

    private void checkAccess(Event event, User user) {
        if (event.getMatrixRoomId() == null) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "This event does not have a chat"
            );
        }

        boolean isCreator = event.getCreatorId().equals(user.getId());

        boolean isParticipant =
            participationRepository.existsByEventIdAndUserId(
                event.getId(),
                user.getId()
            );

        if (!isCreator && !isParticipant) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "You are not a participant of this event"
            );
        }
    }

    public record SendMessageRequest(String message) {}
}
