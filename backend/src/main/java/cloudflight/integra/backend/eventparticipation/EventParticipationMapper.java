package cloudflight.integra.backend.eventparticipation;

import cloudflight.integra.backend.event.EventMapper;
import cloudflight.integra.backend.event.model.Event;
import cloudflight.integra.backend.event.model.PendingReviewDto;
import cloudflight.integra.backend.eventparticipation.model.CreateEventParticipationDto;
import cloudflight.integra.backend.eventparticipation.model.EventParticipation;
import cloudflight.integra.backend.eventparticipation.model.EventParticipationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface EventParticipationMapper {
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    EventParticipationDto toDto(EventParticipation eventParticipation);

    @Mapping(source = "eventId", target = "event")
    EventParticipation toEntity(CreateEventParticipationDto request);

    @Mapping(target = "participationId", source = "id")
    PendingReviewDto toPendingReviewDto(EventParticipation eventParticipation);

    default Event eventFromId(Long id) {
        if (id == null) return null;
        return new Event().setId(id);
    }
}

