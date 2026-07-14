package cloudflight.integra.backend.event;

import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.EventDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDto toDto(Event event);
    Event toEntity(EventDto eventDto);
}
