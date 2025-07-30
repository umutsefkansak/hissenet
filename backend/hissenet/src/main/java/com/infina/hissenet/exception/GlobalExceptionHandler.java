package com.infina.hissenet.exception;

import com.infina.hissenet.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/*
Buradaki hata yönetimimiz şu şekilde olacak
RuntimeException'dan türeyen birden fazla özel hatayı tek bir yerden yakalayacak.
Böylece her özel exception için ayrı metot yazmamıza gerek kalmaz ve kod daha sade, esnek hale gelecek.
Aşağıdaki örnekte olduğu gibi birden fazla exception sınıfı birlikte handle edilebilir:
@ExceptionHandler({FailedToFieldException.class, CloudinaryException.class, RuntimeException.class, MaxFilesException.class, OnlyImageException.class})
public ResponseEntity<ApiResponse<Void>> badRequestException(RuntimeException ex, HttpServletRequest http)
çünkü hepsi runtimedan extends ediliyor.
*/
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 400 bad request
    // 401 unauthorized
    // 403 forbidden
    // 404 not found
    @ExceptionHandler({NotFoundException.class,UserNotFoundException.class})
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex,HttpServletRequest http) {
        ApiResponse<Void> response=new ApiResponse<>(
                404,
                http.getRequestURI(),
                "Bulunamadi hatasi",
                LocalDateTime.now(),
                null,
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    // 429 many request
    // validation exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> validationException(MethodArgumentNotValidException ex, HttpServletRequest http) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponse<Void> response = new ApiResponse<>(
                400,
                http.getRequestURI(),
                "Doğrulama Hatası",
                LocalDateTime.now(),
                errors,
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


}
