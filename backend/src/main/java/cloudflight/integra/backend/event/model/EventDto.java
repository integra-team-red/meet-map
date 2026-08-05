package cloudflight.integra.backend.event.model;
import java.time.LocalDateTime;
import java.util.List;

public record EventDto (Long id,
                        String title,
                        String description,
                        String address,
                        String city,
                        Double latitude,
                        Double longitude,
                        LocalDateTime dateTime,
                        Integer maxParticipants,
                        Integer minAge,
                        Integer maxAge,
                        EventStatus status,
                        Long creatorId,
                        LocalDateTime createdAt,
                        List<Long> tagIds) {
}
