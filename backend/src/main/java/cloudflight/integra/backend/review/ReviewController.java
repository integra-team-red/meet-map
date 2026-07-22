package cloudflight.integra.backend.review;

import cloudflight.integra.backend.review.model.CreateReviewDto;
import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.review.model.ReviewDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public Page<ReviewDto> getAllForEvent(
        @PathVariable Long eventId,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getForEvent(eventId, pageable).map(mapper::toDto);
    }

    @GetMapping("/reviews/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/events/reviews")
    public ResponseEntity<ReviewDto> create(@Valid @RequestBody CreateReviewDto dto) {
        Review review = mapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(service.create(review)));
    }
}
