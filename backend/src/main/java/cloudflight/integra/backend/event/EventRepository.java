package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying(clearAutomatically = true)
    @Query("""

        UPDATE Event e
        SET e.status = cloudflight.integra.backend.event.model.EventStatus.COMPLETED
        WHERE e.status = cloudflight.integra.backend.event.model.EventStatus.ACTIVE
        AND e.dateTime < :now""")
    int markPastEventsCompleted(@Param("now") LocalDateTime now);

    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN e.tags t
        WHERE (:searchTerm = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:city = '' OR e.city = :city)
        AND COALESCE(e.maxAge, 200) >= :minAge
        AND COALESCE(e.minAge, 0) <= :maxAge
        AND e.dateTime >= :dateFrom
        AND e.dateTime <= :dateTo
        AND (:noTags = true OR t.id IN :tagIds)
        AND (:creatorId IS NULL OR e.creatorId = :creatorId)
        AND (:status IS NULL OR e.status = :status)""")
    Page<Event> findFiltered(
        @Param("searchTerm") String searchTerm,
        @Param("city") String city,
        @Param("tagIds") List<Long> tagIds,
        @Param("noTags") boolean noTags,
        @Param("minAge") Integer minAge,
        @Param("maxAge") Integer maxAge,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        @Param("creatorId") Long creatorId,
        @Param("status") EventStatus status,
        Pageable pageable
    );

    @Query("SELECT DISTINCT city FROM Event")
    List<String> findDistinctCities();
}
