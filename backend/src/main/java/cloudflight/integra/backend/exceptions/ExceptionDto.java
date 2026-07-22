package cloudflight.integra.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;

import java.util.List;

public record ExceptionDto (
    HttpStatus status,
    String message,
    List<FieldError> fieldErrors
) {}
