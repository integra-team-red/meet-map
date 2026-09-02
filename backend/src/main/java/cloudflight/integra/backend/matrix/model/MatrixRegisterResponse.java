package cloudflight.integra.backend.matrix.model;

public record MatrixRegisterResponse(
    String access_token,
    String user_id,
    String home_server,
    String device_id
) {
}
