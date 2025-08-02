package com.infina.hissenet.exception;

import com.infina.hissenet.exception.auth.LoginException;
import com.infina.hissenet.exception.common.NotFoundException;
import com.infina.hissenet.exception.common.RateLimitException;
import com.infina.hissenet.exception.customer.CustomerNotFoundException;
import com.infina.hissenet.exception.customer.EmailAlreadyExistsException;
import com.infina.hissenet.exception.customer.TaxNumberAlreadyExistsException;
import com.infina.hissenet.exception.customer.TcNumberAlreadyExistsException;
import com.infina.hissenet.exception.mail.MailException;
import com.infina.hissenet.exception.mail.MailRateLimitException;
import com.infina.hissenet.exception.mail.VerificationCodeException;
import com.infina.hissenet.exception.mail.VerificationCodeNotFoundException;
import com.infina.hissenet.exception.role.RoleAlreadyExistsException;
import com.infina.hissenet.exception.role.RoleNotFoundException;
import com.infina.hissenet.exception.transaction.TransactionAlreadyCancelledException;
import com.infina.hissenet.exception.transaction.TransactionAlreadyCompletedException;
import com.infina.hissenet.exception.wallet.*;
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
            VerificationCodeNotFoundException.class,
            RoleNotFoundException.class,
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
    @ExceptionHandler({RoleAlreadyExistsException.class, InsufficientBalanceException.class, TransactionAlreadyCancelledException.class, TransactionAlreadyCompletedException.class, WalletAlreadyExistsException.class, WalletNotActiveException.class, EmailAlreadyExistsException.class, TcNumberAlreadyExistsException.class, TaxNumberAlreadyExistsException.class})
    public ProblemDetail handleConflict(RuntimeException ex) {
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

    // 429 - Too Many Requests (Rate Limit) and limit exceptiom
    @ExceptionHandler({RateLimitException.class, WalletLimitExceededException.class, MailRateLimitException.class})
    public ProblemDetail rateLimitException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problem.setTitle("Too Many Requests");
        problem.setType(URI.create("https://www.hissenet.com/errors/rate-limit"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
    // 423 Locked Exception
    @ExceptionHandler({WalletLockedException.class})
    public ProblemDetail lockedException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.LOCKED, ex.getMessage());
        problem.setTitle("Too Many Requests");
        problem.setType(URI.create("https://www.hissenet.com/errors/rate-limit"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 422 - Unprocessable Entity (Mail/Verification işlemleri için)
    @ExceptionHandler({MailException.class, VerificationCodeException.class})
    public ProblemDetail handleMailException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Mail Processing Error");
        problem.setType(URI.create("https://www.hissenet.com/errors/mail-processing"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }



}
