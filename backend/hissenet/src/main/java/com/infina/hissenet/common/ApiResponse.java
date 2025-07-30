package com.infina.hissenet.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String path;
    private String message;
    private LocalDateTime localDateTime;
    private Map<String, String> errors;
    private T data;


    public ApiResponse(int status, String path, String message, LocalDateTime localDateTime, Map<String, String> errors, T data) {
        this.status = status;
        this.path = path;
        this.message = message;
        this.localDateTime = localDateTime;
        this.errors = errors;
        this.data = data;
    }


    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(200, null, message, LocalDateTime.now(), null, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, null, message, LocalDateTime.now(), null, data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, null, message, LocalDateTime.now(), null, data);
    }

    public int getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public T getData() {
        return data;
    }


}
