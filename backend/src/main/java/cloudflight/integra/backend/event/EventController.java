package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.CreateEventDto;
import cloudflight.integra.backend.event.model.EventDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    private final EventMapper mapper;

    public EventController(EventService service, EventMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all Events",
        operationId = "getAllEvents"
    )
    public Page<EventDto> getAll(
        @PageableDefault(size = 20, sort = "dateTime") Pageable pageable,
        @RequestParam(defaultValue = "") String searchTerm
    ) {
        return service.getAll(pageable, searchTerm).map(mapper::toDto);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get Event by Id",
        operationId = "getEvent"
    )
    public EventDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Create Event",
        operationId = "createEvent"
    )
    public ResponseEntity<EventDto> create(@Valid @RequestBody CreateEventDto event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(event))));
    }

    @Operation(
        summary = "Update an event",
        operationId = "updateEvent"
    )
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDto update(@PathVariable Long id, @Valid @RequestBody CreateEventDto event) {
        return service.update(id, mapper.toEntity(event)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Delete Event by Id",
        operationId = "deleteEvent"
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
