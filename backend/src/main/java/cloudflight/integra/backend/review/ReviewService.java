package cloudflight.integra.backend.review;


import cloudflight.integra.backend.review.model.Review;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public List<Review> getAll() {
        return repository.findAll();
    }

    public List<Review> getForEvent(Long eventId) {
        return repository.findByEventId(eventId);
    }

    public Optional<Review> getById(Long id) {
        return repository.findById(id);
    }

    public Review create(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5.");
        }
        if (repository.findByUserIdAndEventId(review.getEventId(), review.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already reviewed by this user.");
        }
        review.setCreatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    public Optional<Review> update(Long id, Review review) {
        return repository.findById(id).map(existing -> {
            review.setId(id);
            return repository.save(review);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existing -> {
            repository.deteleById(id);
            return true;
        }).orElse(false);
    }
}
