package cloudflight.integra.backend.eventparticipation.model;

import cloudflight.integra.backend.event.model.EventDto;

import java.time.LocalDateTime;

public record EventParticipationDto(Long id, Long userId, EventDto event, LocalDateTime joinedAt) {
}
