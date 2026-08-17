package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByTitleContainingIgnoreCase(
        String title,
        Pageable pageable
    );
}
