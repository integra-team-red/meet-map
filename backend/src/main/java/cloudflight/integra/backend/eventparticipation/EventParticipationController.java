package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.CreateEventParticipationDto;
import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @GetMapping(value = "/events/{id}/participants", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EventParticipationDto> getAllParticipants(
        @PathVariable Long id,
        @PageableDefault(size = 20, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return service.getParticipants(id, pageable).map(mapper::toDto);
    }

    @PostMapping(value = "/events/{id}/join/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EventParticipationDto> create(@PathVariable Long id, @PathVariable Long userId) {
        CreateEventParticipationDto dto = new CreateEventParticipationDto(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto((service.joinEvent(mapper.toEntity(dto)))));
    }

    @DeleteMapping(value = "/events/{id}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@PathVariable Long id) {
        service.leaveEvent(id);
    }
}
