package cloudflight.integra.backend.review;


import cloudflight.integra.backend.event.EventService;
import cloudflight.integra.backend.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository repository;
    private final EventService eventService;

    public ReviewService(ReviewRepository repository, EventService eventService) {
        this.repository = repository;
        this.eventService = eventService;
    }

    public List<Review> getAll() {
        return repository.findAll();
    }

    public Page<Review> getForEvent(Long eventId, Pageable pageable) {
        return repository.findAllByEventId(eventId, pageable);
    }

    public Optional<Review> getById(Long id) {
        return repository.findById(id);
    }

    public Review create(Review review) {
        if (eventService.getById(review.getEvent().getId()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event does not exist");
        }
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5.");
        }
        if (repository.findReviewsByEventAndUserId(review.getEvent(), review.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already reviewed by this user.");
        }
        review.setCreatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    public Optional<Review> update(Long id, Review review) {
        return repository.findById(id).map(_ -> {
            review.setId(id);
            return repository.save(review);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(_ -> {
            repository.deleteById(id);
            return true;
        }).orElse(false);
    }
}
