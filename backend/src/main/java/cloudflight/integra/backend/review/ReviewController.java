package cloudflight.integra.backend.review;

import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.review.model.ReviewDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService service;
    private final ReviewMapper mapper;

    public ReviewController(ReviewService service, ReviewMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping("/events/{eventId}/reviews")
    public List<ReviewDto> getAllForEvent(@PathVariable Long eventId) {
        return service.getForEvent(eventId).stream().map(mapper::toDto).toList();
    }

    @GetMapping("/reviews/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/events/{eventId}/reviews")
    public ResponseEntity<ReviewDto> create(@PathVariable Long eventId, @RequestBody ReviewDto dto) {
        Review review = mapper.toEntity(dto).setEventId(eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(review)));
    }
}
