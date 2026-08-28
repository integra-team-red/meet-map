package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EventParticipationController {
    private final EventParticipationService service;
    private final EventParticipationMapper mapper;

    public EventParticipationController(
        EventParticipationService service,
        EventParticipationMapper mapper
    ) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = "/events/{id}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EventParticipationDto> getAllParticipants(
        @PathVariable Long id,
        @PageableDefault(size = 20, direction = Sort.Direction.ASC) Pageable pageable
    ) {

        return service.getParticipants(id, pageable).map(mapper::toDto);
    }

    @Operation(operationId = "joinEvent", summary = "Join an event")
    @PostMapping(value = "/events/{id}/join", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventParticipationDto> create(@PathVariable Long id, Authentication authentication) {
        EventParticipation participation = service.joinEvent(id, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(participation));
    }

    @Operation(operationId = "leaveEvent", summary = "Leave an event")
    @DeleteMapping(value = "/events/{id}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable Long id, Authentication authentication) {
        service.leaveEvent(id, authentication.getName());
    }
}
