package cloudflight.integra.backend.matrix.api;

public record MatrixRegisterUserRequest(
    String nonce,
    String username,
    String password,
    boolean admin,
    String mac
) {
}
