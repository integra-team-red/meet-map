package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT DISTINCT e FROM Event e
        LEFT JOIN e.tags t
        WHERE (:searchTerm = '' OR LOWER(e.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
        AND (:city = '' OR e.city = :city)
        AND COALESCE(e.maxAge, 200) >= :minAge
        AND COALESCE(e.minAge, 0) <= :maxAge
        AND e.dateTime >= :dateFrom
        AND e.dateTime <= :dateTo
        AND (:noTags = true OR t.id IN :tagIds)""")
    Page<Event> findFiltered(
        @Param("searchTerm") String searchTerm,
        @Param("city") String city,
        @Param("tagIds") List<Long> tagIds,
        @Param("noTags") boolean noTags,
        @Param("minAge") Integer minAge,
        @Param("maxAge") Integer maxAge,
        @Param("dateFrom") LocalDateTime dateFrom,
        @Param("dateTo") LocalDateTime dateTo,
        Pageable pageable);

    @Query("SELECT DISTINCT city FROM Event")
    List<String> findDistinctCities();

}
