package cloudflight.integra.backend.event;
import cloudflight.integra.backend.event.model.CreateEventDto;
import cloudflight.integra.backend.event.model.EventDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    private final EventMapper mapper;

    public EventController(EventService service, EventMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<EventDto> getAll() {
        return service.getAll().stream().map(mapper::toDto).toList();
    }

    @GetMapping("/{id}")
    public EventDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody CreateEventDto event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(event))));
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable Long id, @Valid @RequestBody CreateEventDto event) {
        return service.update(id, mapper.toEntity(event)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
