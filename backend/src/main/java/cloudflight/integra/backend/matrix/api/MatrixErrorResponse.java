package cloudflight.integra.backend.matrix.api;

public record MatrixErrorResponse (
    String errcode,
    String error
){
}
