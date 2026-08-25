package cloudflight.integra.backend.review;

import cloudflight.integra.backend.event.EventMapper;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.review.model.CreateReviewDto;
import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.review.model.ReviewDto;
import cloudflight.integra.backend.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.Objects;
import java.util.stream.Stream;


@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface ReviewMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "authorName", source = "user")
    ReviewDto toDto(Review review);

    @Mapping(source = "eventId", target = "event")
    @Mapping(source = "userId", target = "user")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(CreateReviewDto request);

    default Event eventFromId(Long id) {
        if (id == null) return null;
        return new Event().setId(id);
    }

    default User userFromId(Long id) {
        if (id == null) return null;
        return new User().setId(id);
    }

    default String authorName(User user) {
        if (user == null) return "";
        String firstName = Objects.requireNonNullElse(user.getFirstName(), "");
        String lastName = Objects.requireNonNullElse(user.getLastName(), "");
        return (firstName + " " + lastName).trim();
    }
}
