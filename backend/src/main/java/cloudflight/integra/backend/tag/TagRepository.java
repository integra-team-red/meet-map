package cloudflight.integra.backend.tag;

import cloudflight.integra.backend.tag.model.Category;
import cloudflight.integra.backend.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByCategory(Category category);
    Optional<Tag> findByName(String name);
}
