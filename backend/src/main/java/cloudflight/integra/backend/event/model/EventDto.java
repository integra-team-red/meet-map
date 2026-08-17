package cloudflight.integra.backend.event.model;

import cloudflight.integra.backend.flag.model.FlagDto;
import cloudflight.integra.backend.tag.model.TagDto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public record EventDto (Long id,
                        String title,
                        String description,
                        String address,
                        String city,
                        Double latitude,
                        Double longitude,
                        Instant dateTime,
                        Integer maxParticipants,
                        Integer minAge,
                        Integer maxAge,
                        EventStatus status,
                        Long creatorId,
                        LocalDateTime createdAt,
                        List<TagDto> tags,
                        List<FlagDto> flags) {
}
