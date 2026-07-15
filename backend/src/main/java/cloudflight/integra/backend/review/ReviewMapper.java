package cloudflight.integra.backend.review;

import cloudflight.integra.backend.event.EventMapper;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.review.model.CreateReviewDto;
import cloudflight.integra.backend.review.model.Review;
import cloudflight.integra.backend.review.model.ReviewDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface ReviewMapper {
    ReviewDto toDto(Review review);

    @Mapping(source = "eventId", target = "event")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(CreateReviewDto request);

    default Event eventFromId(Long id) {
        if (id == null) return null;
        return new Event().setId(id);
    }
}
