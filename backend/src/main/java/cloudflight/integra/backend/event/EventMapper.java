package cloudflight.integra.backend.event;
import cloudflight.integra.backend.event.model.CreateEventDto;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventDto;
import cloudflight.integra.backend.tag.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventDto toDto(Event event);
    Event toEntity(EventDto eventDto);

    @Mapping(target = "tags", source = "tagIds")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event toEntity(CreateEventDto dto);


    default Instant toUtc(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }

    default LocalDateTime toLocalUtc(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    default Long tagToId(Tag tag) {
        if (tag == null) {
            return null;
        }
        return tag.getId();
    }

    default Tag idToTag(Long id) {
        if (id == null) {
            return null;
        }
        Tag tag = new Tag();
        tag.setId(id);
        return tag;
    }
}
