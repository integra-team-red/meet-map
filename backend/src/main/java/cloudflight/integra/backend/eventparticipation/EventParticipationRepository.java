package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    @EntityGraph(attributePaths = "user")
    Page<EventParticipation> findByEventId(Long eventId, Pageable pageable);

    void deleteById(@NonNull Long id);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    Optional<EventParticipation> findByEventIdAndUserId(Long eventId, Long userId);

    long countByEventId(Long eventId);

    @Query("SELECT ep.userId FROM EventParticipation ep LEFT JOIN Event e ON ep.event.id = e.id WHERE e.id = :eventId")
    Page<Long> findAllUserIdsByEventId(Long eventId, Pageable pageable);
    @Query(
        "SELECT e FROM Event e " +
        "LEFT JOIN EventParticipation ep ON ep.event.id = e.id " +
            "WHERE ep.userId = :participantId"
    )
    Page<Event> findAllEventsByParticipant(Long participantId, Pageable pageable);

    @Query("SELECT ep FROM EventParticipation ep " +
        "WHERE ep.userId = :userId " +
        "AND ep.event.status = cloudflight.integra.backend.event.model.EventStatus.COMPLETED " +
        "AND ep.reviewDismissed = false " +
        "AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.event = ep.event AND r.user.id = :userId) " +
        "ORDER BY ep.event.dateTime DESC")
    List<EventParticipation> findPendingReviews(@Param("userId") Long userId, Pageable pageable);

}

