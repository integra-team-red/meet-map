package cloudflight.integra.backend.user;

import cloudflight.integra.backend.tag.TagMapper;
import cloudflight.integra.backend.tag.model.TagDto;
import cloudflight.integra.backend.user.model.UpdateUserTagsDto;
import cloudflight.integra.backend.user.model.UserDto;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private final TagMapper tagMapper;

    public UserController(UserService service, UserMapper mapper, TagMapper tagMapper) {
        this.service = service;
        this.mapper = mapper;
        this.tagMapper = tagMapper;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getCurrentUser(Authentication authentication) {
        return mapper.toDto(service.getByEmail(authentication.getName()));
    }

    @GetMapping(value = "/me/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> getTags(Authentication authentication) {
        return service.getTags(authentication.getName())
            .stream()
            .map(tagMapper::toDto)
            .toList();
    }

    @PutMapping(value = "/me/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> updateTags(
        @Valid @RequestBody UpdateUserTagsDto updateUserTagsDto,
        Authentication authentication
    ) {
        Set<Long> tagIds = new HashSet<>(updateUserTagsDto.tagIds());

        return service.updateTags(authentication.getName(), tagIds)
            .getTags()
            .stream()
            .map(tagMapper::toDto)
            .toList();
    }
}
