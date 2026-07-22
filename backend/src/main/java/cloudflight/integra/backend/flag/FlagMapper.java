package cloudflight.integra.backend.flag;

import cloudflight.integra.backend.event.EventMapper;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.flag.model.CreateFlagDto;
import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.flag.model.FlagDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface FlagMapper {
    FlagDto toDto(Flag flag);

    @Mapping(source = "eventId", target = "event.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Flag toEntity(CreateFlagDto dto);

    default Event eventFromId(Long id) {
        if (id == null) return null;
        return new Event().setId(id);
    }
}
