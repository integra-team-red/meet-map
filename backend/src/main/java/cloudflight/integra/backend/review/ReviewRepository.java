package cloudflight.integra.backend.review;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByEventId(Long eventId);

    Optional<Review> findReviewsByEventAndUserId(Event event, Long userId);
}
