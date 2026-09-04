package cloudflight.integra.backend.matrix.model;

public record MatrixRegisterRequest(
    String nonce,
    String username,
    String password,
    boolean admin,
    String mac
) {
}
