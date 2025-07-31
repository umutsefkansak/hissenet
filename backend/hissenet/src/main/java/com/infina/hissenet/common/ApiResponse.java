package com.infina.hissenet.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;



@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private int status;
    private String path;
    private String message;
    private LocalDateTime localDateTime;
    private T data;


    public ApiResponse(int status, String path, String message, LocalDateTime localDateTime, T data) {
        this.status = status;
        this.path = path;
        this.message = message;
        this.localDateTime = localDateTime;
        this.data = data;
    }


    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(200, null, message, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(200, null, message, LocalDateTime.now(), data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, null, message, LocalDateTime.now(), data);
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

    public T getData() {
        return data;
    }


}
