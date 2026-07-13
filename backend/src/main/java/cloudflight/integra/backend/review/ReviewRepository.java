package cloudflight.integra.backend.review;

import cloudflight.integra.backend.review.model.Review;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ReviewRepository {
    private final Map<Long, Review> reviews = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    public List<Review> findAll() {
        return new ArrayList<>(reviews.values());
    }

    public List<Review> findByEventId(Long eventId) {
        return reviews.values().stream().filter(review -> review.getEventId().equals(eventId)).toList();
    }

    public Optional<Review> findByUserIdAndEventId(Long eventId, Long userId) {
        return reviews.values().stream().filter(review -> review.getEventId().equals(eventId) &&
            review.getUserId().equals(userId)).findFirst();
    }

    public Optional<Review> findById(Long id) {
        return Optional.ofNullable(reviews.get(id));
    }


    public Review save(Review review) {
        if (review.getId() == null) {
            review.setId(idGen.getAndIncrement());
        }
        reviews.put(review.getId(), review);
        return review;
    }

    public void deteleById(Long id) {
        reviews.remove(id);
    }
}
