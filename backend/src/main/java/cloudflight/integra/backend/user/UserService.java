package cloudflight.integra.backend.user;

import cloudflight.integra.backend.tag.TagRepository;
import cloudflight.integra.backend.tag.model.Tag;
import cloudflight.integra.backend.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository repository;
    private final TagRepository tagRepository;

    public UserService(UserRepository repository, TagRepository tagRepository) {
        this.repository = repository;
        this.tagRepository = tagRepository;
    }

    public User getByEmail(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new
                ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public Set<Tag> getTags(String email) {
        return repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
            .getTags();
    }

    public User updateTags(String email, Set<Long> tagIds) {
        User user = repository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<Tag> tagList = tagRepository.findAllById(tagIds);
        if (tagIds.size() != tagList.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more tags not found");
        }
        Set<Tag> newTags = new HashSet<>(tagList);
        user.setTags(newTags);
        return repository.save(user);
    }
}
