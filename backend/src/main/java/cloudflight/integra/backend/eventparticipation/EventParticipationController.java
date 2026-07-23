package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.CreateEventParticipationDto;
import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Page<Long> getAllParticipants(
        @PathVariable Long id,
        @PageableDefault(size = 20, direction = Sort.Direction.ASC) Pageable pageable) {
        return service.getParticipants(id, pageable);
    }

    @PostMapping("/events/{id}/join/{userId}")
    public ResponseEntity<EventParticipationDto> create(@PathVariable Long id, @PathVariable Long userId) {
        CreateEventParticipationDto dto = new CreateEventParticipationDto(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto((service.joinEvent(mapper.toEntity(dto)))));
    }

    @DeleteMapping("/events/{id}/leave")
    public void delete(@PathVariable Long id) {
        service.leaveEvent(id);
    }

}
