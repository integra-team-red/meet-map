package cloudflight.integra.backend.flag.model;

import cloudflight.integra.backend.event.model.EventDto;

public record FlagDto(Long id, Long userId, Long eventId, String reason) {
}
