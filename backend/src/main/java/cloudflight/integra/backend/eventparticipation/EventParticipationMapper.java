package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventParticipationMapper {
    EventParticipationDto toDto(EventParticipation eventParticipation);

    EventParticipation toEntity(EventParticipationDto eventParticipationDto);
}

