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
import com.infina.hissenet.exception.order.IllegalStateException;
import com.infina.hissenet.exception.riskassessment.IncompleteAssessmentException;
import com.infina.hissenet.exception.riskassessment.InvalidAnswerException;
import com.infina.hissenet.exception.riskassessment.RiskAssessmentException;
import com.infina.hissenet.exception.role.RoleAlreadyExistsException;
import com.infina.hissenet.exception.role.RoleNotFoundException;
import com.infina.hissenet.exception.transaction.InsufficientStockException;
import com.infina.hissenet.exception.transaction.TransactionAlreadyCancelledException;
import com.infina.hissenet.exception.transaction.TransactionAlreadyCompletedException;
import com.infina.hissenet.exception.transaction.UnauthorizedOperationException;
import com.infina.hissenet.exception.wallet.*;
import com.infina.hissenet.utils.MessageUtils;
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

/**
 * Centralized REST exception handling producing RFC 7807 Problem Details
 * responses with localized titles and consistent structure.
 *
 * Responsibilities:
 * - Maps domain/business exceptions to appropriate HTTP status codes
 * - Includes timestamps and error details for client diagnostics
 * - Uses MessageUtils for i18n-ready titles
 *
 * Author: Furkan Can
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Not Found
    @ExceptionHandler({
            NotFoundException.class,
    })
    public ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.not.found"));
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

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, MessageUtils.getMessage("common.validation.failed"));
        problem.setTitle(MessageUtils.getMessage("error.title.validation"));
        problem.setType(URI.create("https://www.hissenet.com/errors/validation"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 409 - Conflict
    @ExceptionHandler({RoleAlreadyExistsException.class, InsufficientBalanceException.class, TransactionAlreadyCancelledException.class, TransactionAlreadyCompletedException.class, WalletAlreadyExistsException.class, WalletNotActiveException.class, EmailAlreadyExistsException.class, TcNumberAlreadyExistsException.class, TaxNumberAlreadyExistsException.class})
    public ProblemDetail handleConflict(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.conflict"));
        problem.setType(URI.create("https://www.hissenet.com/errors/conflict"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 500 - Internal Server Error
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, MessageUtils.getMessage("common.internal.error"));
        problem.setTitle(MessageUtils.getMessage("error.title.internal"));
        problem.setType(URI.create("https://www.hissenet.com/errors/internal"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 401 - Unauthorized
    @ExceptionHandler(LoginException.class)
    public ProblemDetail unauthorizedException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.unauthorized"));
        problem.setType(URI.create("https://www.hissenet.com/errors/unauthorized"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 429 - Too Many Requests (Rate Limit) and limit exceptiom
    @ExceptionHandler({RateLimitException.class, WalletLimitExceededException.class, MailRateLimitException.class})
    public ProblemDetail rateLimitException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.rate.limit"));
        problem.setType(URI.create("https://www.hissenet.com/errors/rate-limit"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
    // 423 Locked Exception
    @ExceptionHandler({WalletLockedException.class, IllegalStateException.class})
    public ProblemDetail lockedException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.LOCKED, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.locked"));
        problem.setType(URI.create("https://www.hissenet.com/errors/locked"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 422 - Unprocessable Entity (Mail/Verification işlemleri için)
    @ExceptionHandler({MailException.class, VerificationCodeException.class})
    public ProblemDetail handleMailException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.mail.processing"));
        problem.setType(URI.create("https://www.hissenet.com/errors/mail-processing"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 422 - Unprocessable Entity (Risk Assessment için)
    @ExceptionHandler({
            RiskAssessmentException.class,
            InvalidAnswerException.class,
            IncompleteAssessmentException.class
    })
    public ProblemDetail handleRiskAssessmentException(RiskAssessmentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.risk.assessment"));
        problem.setType(URI.create("https://www.hissenet.com/errors/risk-assessment"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }
    // 403 Forbiden
    @ExceptionHandler({
            UnauthorizedOperationException.class
    })
    public ProblemDetail forbiddenException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.forbidden"));
        problem.setType(URI.create("https://www.hissenet.com/errors/forbidden"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }

    // 400 Bad Request
    @ExceptionHandler({
            InsufficientStockException.class
    })
    public ProblemDetail badRequestException(RuntimeException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle(MessageUtils.getMessage("error.title.bad.request"));
        problem.setType(URI.create("https://www.hissenet.com/errors/bad-request"));
        problem.setProperty("timestamp", LocalDateTime.now());
        return problem;
    }



}
