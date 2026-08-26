package cloudflight.integra.backend.user;

import cloudflight.integra.backend.event.EventMapper;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventDto;
import cloudflight.integra.backend.eventparticipation.EventParticipationService;
import cloudflight.integra.backend.review.ReviewService;
import cloudflight.integra.backend.review.model.EventAverageRating;
import cloudflight.integra.backend.tag.TagMapper;
import cloudflight.integra.backend.tag.model.TagDto;
import cloudflight.integra.backend.user.model.UpdateUserTagsDto;
import cloudflight.integra.backend.user.model.UserDto;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private final TagMapper tagMapper;

    private final EventParticipationService epService;
    private final EventMapper eventMapper;
    private final ReviewService reviewService;

    public UserController(
        UserService service,
        UserMapper mapper,
        EventParticipationService epService,
        EventMapper eventMapper,
        TagMapper tagMapper,
        ReviewService reviewService
    ) {
        this.service = service;
        this.mapper = mapper;
        this.epService = epService;
        this.eventMapper = eventMapper;
        this.tagMapper = tagMapper;
        this.reviewService = reviewService;
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getCurrentUser(Authentication authentication) {
        return mapper.toDto(service.getByEmail(authentication.getName()));
    }

    @GetMapping(value = "/me/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> getTags(Authentication authentication) {
        return service.getTags(authentication.getName()).stream().map(tagMapper::toDto).toList();
    }

    @PutMapping(value = "/me/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TagDto> updateTags(
        @Valid @RequestBody UpdateUserTagsDto updateUserTagsDto,
        Authentication authentication
    ) {
        Set<Long> tagIds = new HashSet<>(updateUserTagsDto.tagIds());

        return service.updateTags(authentication.getName(), tagIds).getTags().stream().map(tagMapper::toDto).toList();
    }

    @GetMapping(value = "/{userId}/joined", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<EventDto> getJoinedEvents(
        @PageableDefault(size = 20, sort = "dateTime") Pageable pageable,
        @PathVariable Long userId
    ) {
        Page<Event> events = epService.getEventsByParticipant(userId, pageable);
        Map<Long, EventAverageRating> ratings = reviewService.getAverageRatings(events.getContent()
            .stream().map(Event::getId).toList());
        return events.map(event -> eventMapper.toDto(event, ratings.get(event.getId())));

    }
}
