package com.infina.hissenet.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Not Found
    @ExceptionHandler({
            NotFoundException.class,
            UserNotFoundException.class,
            AddressNotFoundException.class,
            CustomerNotFoundException.class
    })
    public ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://www.hissenet.com/errors/not-found"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 400 - Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://www.hissenet.com/errors/validation"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 409 - Conflict
    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ProblemDetail handleConflict(RoleAlreadyExistsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle("Conflict Error");
        problem.setType(URI.create("https://www.hissenet.com/errors/conflict"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problem.setTitle("Internal Server Error");
        problem.setType(URI.create("https://www.hissenet.com/errors/internal"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 401 - Unauthorized
    @ExceptionHandler(LoginException.class)
    public ProblemDetail unauthorizedException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle("Unauthorized");
        problem.setType(URI.create("https://www.hissenet.com/errors/unauthorized"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 429 - Too Many Requests (Rate Limit)
    @ExceptionHandler(RateLimitException.class)
    public ProblemDetail rateLimitException(RateLimitException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problem.setTitle("Too Many Requests");
        problem.setType(URI.create("https://www.hissenet.com/errors/rate-limit"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
}
