package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventParticipationController {
    private final EventParticipationService service;
    private final EventParticipationMapper mapper;

    public EventParticipationController(EventParticipationService service, EventParticipationMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/events/{id}/participants")
    public List<Long> getAllParticipants(@PathVariable Long id) {
        return service.getParticipants(id);
    }

    @PostMapping("/events/{id}/join/{userId}")
    public ResponseEntity<EventParticipationDto> create(@PathVariable Long id, @PathVariable Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto((service.joinEvent(id, userId))));
    }

    @DeleteMapping("/events/{id}/leave/{userId}")
    public void delete(@PathVariable Long id, @PathVariable Long userId) {
        service.leaveEvent(id, userId);
    }

}
