package cloudflight.integra.backend.event;
import cloudflight.integra.backend.event.model.CreateEventDto;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventDto;
import cloudflight.integra.backend.tag.model.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "tagIds", source = "tags")
    EventDto toDto(Event event);

    @Mapping(target = "tags", source = "tagIds")
    Event toEntity(EventDto eventDto);

    @Mapping(target = "tags", source = "tagIds")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Event toEntity(CreateEventDto dto);


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
