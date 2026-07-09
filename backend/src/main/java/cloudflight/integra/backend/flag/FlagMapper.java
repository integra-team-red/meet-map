package cloudflight.integra.backend.flag;

import cloudflight.integra.backend.flag.model.Flag;
import cloudflight.integra.backend.flag.model.FlagDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FlagMapper {
    FlagDto toDto(Flag flag);

    Flag toEntity(FlagDto dto);
}
