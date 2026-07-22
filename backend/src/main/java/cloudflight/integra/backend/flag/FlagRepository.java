package cloudflight.integra.backend.flag;


import cloudflight.integra.backend.flag.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {
}
