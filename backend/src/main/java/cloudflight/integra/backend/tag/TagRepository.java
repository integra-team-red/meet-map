package cloudflight.integra.backend.tag;

import cloudflight.integra.backend.tag.model.Category;
import cloudflight.integra.backend.tag.model.Tag;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TagRepository {
    private final Map<Long, Tag> tags = new HashMap<>();
    private final AtomicLong idGen =  new AtomicLong(1);

    public List<Tag> findAll() {
        return new ArrayList<>(tags.values());
    }

    public List<Tag> findAllByCategory(String category) {
        return tags.values().stream().filter(tag -> tag.getCategory().equals(Category.valueOf(category))).toList();
    }

    public Optional<Tag> findByName(String name) {
        return tags.values().stream().filter(tag -> tag.getName().equals(name)).findAny();
    }

    public Tag save(Tag tag) {
        if (tag.getId() == null){
            tag.setId(idGen.incrementAndGet());
        }
        tags.put(tag.getId(), tag);
        return tag;
    }

    public void deleteById(Long id) {
        tags.remove(id);
    }
}
