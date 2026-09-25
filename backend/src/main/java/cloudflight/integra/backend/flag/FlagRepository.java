package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.flag.model.Flag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {
    Page<Flag> findByEvent(Pageable pageable, Event event);

    void deleteByUserId(Long userId);
}
