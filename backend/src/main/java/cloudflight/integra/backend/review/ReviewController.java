package cloudflight.integra.backend.review;

import cloudflight.integra.backend.auth.CustomUserDetails;
import cloudflight.integra.backend.review.model.CreateReviewDto;
import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.review.model.ReviewDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping(value = "/events/{eventId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get all reviews for an event",
        operationId = "getAllReviewsForEvent"
    )
    public Page<ReviewDto> getAllForEvent(
        @PathVariable Long eventId,
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.getForEvent(eventId, pageable).map(mapper::toDto);
    }

    @GetMapping(value = "/reviews/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get a review by ID",
        operationId = "getReview"
    )
    public ReviewDto getById(@PathVariable Long id) {
        return service.getById(id).map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping(path = "/events/{eventId}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Create a new review for an event",
        operationId = "createReview"
    )
    public ResponseEntity<ReviewDto> create(
        @PathVariable Long eventId,
        @Valid @RequestBody CreateReviewDto dto,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        Review review = mapper.toEntity(dto)
            .setEvent(mapper.eventFromId(eventId))
            .setUser(mapper.userFromId(user.id()));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toDto(service.create(review)));
    }

    @Operation(summary = "Update a review", operationId = "updateReview")
    @PutMapping(path = "/events/{eventId}/reviews/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ReviewDto update(
        @PathVariable Long eventId,
        @PathVariable Long reviewId,
        @Valid @RequestBody CreateReviewDto dto,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return service.update(eventId, reviewId, mapper.toEntity(dto), user.id())
            .map(mapper::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete a review", operationId = "deleteReview")
    @DeleteMapping(path = "/events/{eventId}/reviews/{reviewId}")
    public ResponseEntity<Void> delete(
        @PathVariable Long eventId,
        @PathVariable Long reviewId,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        return service.delete(eventId, reviewId, user.id())
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

}
