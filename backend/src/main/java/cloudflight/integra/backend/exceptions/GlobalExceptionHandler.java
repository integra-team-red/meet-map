package cloudflight.integra.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ExceptionDto(HttpStatus.BAD_REQUEST, e.getBody().getTitle(), e.getFieldErrors()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionDto> handleNoResourceFoundException(NoResourceFoundException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionDto(HttpStatus.NOT_FOUND, e.getBody().getTitle(), null));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionDto> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ExceptionDto(HttpStatus.NOT_FOUND, e.getMessage(), null));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDto> handleResponseStatusException(ResponseStatusException e) {
        return ResponseEntity
            .status(e.getStatusCode())
            .body(new ExceptionDto((HttpStatus) e.getStatusCode(), e.getReason(), null));
    }

}
