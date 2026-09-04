package cloudflight.integra.backend.user.model;

public record UserDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    String mxId,
    String mxPassword
) {
}
