package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.CreateEventDto;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventDto;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.review.ReviewService;
import cloudflight.integra.backend.review.model.EventAverageRating;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService service;
    private final EventMapper mapper;
    private final ReviewService reviewService;

    public EventController(EventService service, EventMapper mapper, ReviewService reviewService) {
        this.service = service;
        this.mapper = mapper;
        this.reviewService = reviewService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all Events", operationId = "getAllEvents")
    public Page<EventDto> getAll(
        @PageableDefault(size = 20, sort = "dateTime") Pageable pageable,
        @RequestParam(defaultValue = "") String searchTerm,
        @RequestParam(defaultValue = "") String city,
        @RequestParam(required = false) List<Long> tagIds,
        @RequestParam(defaultValue = "0") Integer minAge,
        @RequestParam(defaultValue = "200") Integer maxAge,
        @RequestParam(defaultValue = "1900-01-01") LocalDate dateFrom,
        @RequestParam(defaultValue = "2999-12-31") LocalDate dateTo,
        @RequestParam(required = false) Long creatorId,
        @RequestParam(required = false) EventStatus status
    ) {
        Page<Event> events = service.getAll(
            pageable, searchTerm, city, tagIds, minAge, maxAge, dateFrom, dateTo, creatorId, status);
        Map<Long, EventAverageRating> ratings = reviewService.getAverageRatings(
            events.getContent().stream().map(Event::getId).toList());
        return events.map(event -> mapper.toDto(event, ratings.get(event.getId())));
    }

    @GetMapping(value = "/cities", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cities of events", operationId = "getCities")
    public List<String> getCities() {
        return service.getCities();
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Event by Id", operationId = "getEvent")
    public EventDto getById(@PathVariable Long id) {
        return service.getById(id).map(event -> mapper.toDto(
                event, reviewService.getAverageRatingForEvent(id)))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create Event", operationId = "createEvent")
    public ResponseEntity<EventDto> create(@Valid @RequestBody CreateEventDto event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(mapper.toEntity(event))));
    }

    @Operation(summary = "Update an event", operationId = "updateEvent")
    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventDto update(@PathVariable Long id, @Valid @RequestBody CreateEventDto event) {
        return service.update(id, mapper.toEntity(event)).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete Event by Id", operationId = "deleteEvent")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
