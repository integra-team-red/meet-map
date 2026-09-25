package cloudflight.integra.backend.matrix.api;

public record MatrixRegisterUserResponse(
    String access_token,
    String user_id,
    String home_server,
    String device_id
) {
}
