package cloudflight.integra.backend.review;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.review.model.EventAverageRating;
import cloudflight.integra.backend.review.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = "user")
    Page<Review> findAllByEventId(Long eventId, Pageable pageable);

    Optional<Review> findReviewsByEventAndUserId(Event event, Long userId);

    @Query("""
        select new cloudflight.integra.backend.review.model.EventAverageRating(r.event.id, avg(r.rating), count(r.id))
        from Review r
        where r.event.id in :eventIds
        group by r.event.id""")
    List<EventAverageRating> findAverageRatingByEventIds(@Param("eventIds") Collection<Long> eventIds);
}
