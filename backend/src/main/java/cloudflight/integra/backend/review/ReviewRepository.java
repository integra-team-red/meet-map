package cloudflight.integra.backend.review;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findAllByEventId(Long eventId, Pageable pageable);
    Optional<Review> findReviewsByEventAndUserId(Event event, Long userId);
}
