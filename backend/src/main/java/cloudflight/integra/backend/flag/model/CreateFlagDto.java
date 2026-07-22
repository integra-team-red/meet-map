package cloudflight.integra.backend.flag.model;

public record CreateFlagDto(Long id, Long userId, Long eventId, String reason) {
}
