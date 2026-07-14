package cloudflight.integra.backend.eventparticipation.model;

import java.time.LocalDateTime;

public record EventParticipationDto(Long id, Long userId, Long eventId, LocalDateTime joinedAt) {
}
