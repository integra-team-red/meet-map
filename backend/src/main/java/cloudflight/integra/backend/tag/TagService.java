package cloudflight.integra.backend.tag;

import cloudflight.integra.backend.tag.model.Category;
import cloudflight.integra.backend.tag.model.Tag;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    private final TagRepository repository;

    public TagService(TagRepository repository){
        this.repository = repository;
    }

    public Tag create(Tag tag) throws Exception {
        String normalizedName = tag.getName().trim().toLowerCase();
        tag.setName(normalizedName);
        Optional<Tag> dupeTag = repository.findByName(tag.getName());
        if(dupeTag.isPresent())
            throw new Exception("Duplicate tag name \"" + normalizedName + "\": tag #" + dupeTag.get().getId());
        return repository.save(tag);
    }

    public List<Tag> getAll(){
        return repository.findAll();
    }

    public List<Tag> getByCategory(Category category) {
        return repository.findAllByCategory(category);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
