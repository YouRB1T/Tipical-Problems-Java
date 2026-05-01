package com.tipicalproblemsjava.common;

import java.util.List;

import com.tipicalproblemsjava.phone.PhoneNotFoundException;
import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PhoneNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(PhoneNotFoundException exception) {
        log.warn("Not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiError.of(404, "Not Found", List.of(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleBodyValidation(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        log.debug("Request body validation failed: {}", details);
        return ResponseEntity.badRequest()
                .body(ApiError.of(400, "Validation failed", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleParameterValidation(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();
        log.debug("Request parameter validation failed: {}", details);
        return ResponseEntity.badRequest()
                .body(ApiError.of(400, "Validation failed", details));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
        log.warn("Illegal argument: {}", exception.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiError.of(400, "Bad request", List.of(exception.getMessage())));
    }
}
