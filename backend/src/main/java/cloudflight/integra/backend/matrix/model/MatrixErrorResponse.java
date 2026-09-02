package cloudflight.integra.backend.matrix.model;

public record MatrixErrorResponse (
    String errcode,
    String error
){
}
