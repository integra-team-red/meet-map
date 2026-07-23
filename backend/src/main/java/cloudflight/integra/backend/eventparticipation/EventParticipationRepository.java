package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    List<Long> findByEventId(Long eventId);
    void deleteById(@NonNull Long id);
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    long countByEventId(Long eventId);
    @Query("SELECT ep.userId FROM EventParticipation ep LEFT JOIN Event e ON ep.event.id = e.id WHERE e.id = :eventId")
    Page<Long> findAllUserIdsByEventId(Long eventId, Pageable pageable);
}

