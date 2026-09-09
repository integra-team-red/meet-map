package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventStatus;
import cloudflight.integra.backend.tag.model.Tag;
import jakarta.persistence.criteria.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
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

    static Specification<Event> orderByNearest(@NotNull Double latitude, @NotNull Double longitude) {
        return (root, query, builder) -> {
            if (query != null && query.getResultType() != Long.class) {
                query.orderBy(builder.asc(builder.function("distance", Double.class,
                    root.get("latitude").as(Double.class),
                    builder.literal(latitude),
                    root.get("longitude").as(Double.class),
                    builder.literal(longitude)
                )));
            }
            return builder.conjunction();
        };
    }

    static Specification<Event> search(String searchTerm) {
        return (root, _, b) -> searchTerm == null || searchTerm.isBlank()
                ? b.conjunction()
                : b.like(
                    b.lower(root.get("title")),
                    b.lower(b.literal("%" + searchTerm + "%")
                    ));
    }

    static Specification<Event> hasCity(String city) {
        return (root, _, builder) ->
            city == null || city.isBlank() ? builder.conjunction() : builder.equal(root.get("city"), city);
    }

    static Specification<Event> hasTags(List<Long> tagIds) {
        return (root, query, builder) -> {
            if(query == null || tagIds == null || tagIds.isEmpty()) return builder.conjunction();

            query.distinct(false);
            Subquery<Long> sub = query.subquery(Long.class);
            Root<Event> subRoot = sub.from(Event.class);
            Join<Event, Tag> subTags = subRoot.join("tags");

            sub.select(subRoot.get("id"))
                .where(subTags.get("id").in(tagIds));
            return root.get("id").in(sub);
        };
    }

    static Specification<Event> inAgeRange(Integer minAge, Integer maxAge) {
        return (root, _, builder) ->
            builder.and(
                minAge == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("minAge"), minAge),
                maxAge == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("maxAge"), maxAge)
            );
    }

    static Specification<Event> inDateRange(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return (root, _, builder) ->
            builder.and(
                dateFrom == null ? builder.conjunction() : builder.greaterThanOrEqualTo(root.get("dateTime"), dateFrom),
                dateTo == null ? builder.conjunction() : builder.lessThanOrEqualTo(root.get("dateTime"), dateTo)
            );
    }

    static Specification<Event> hasCreator(Long creatorId) {
        return (root, _, builder) -> creatorId == null
            ? builder.conjunction()
            : builder.equal(root.get("creatorId"), creatorId);
    }

    static Specification<Event> hasStatus(EventStatus status) {
        return (root, _, builder) -> status == null
            ? builder.conjunction()
            : builder.equal(root.get("status"), status);
    }

}
