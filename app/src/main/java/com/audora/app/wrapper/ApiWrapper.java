package com.audora.app.wrapper;

import lombok.Data;

@Data
public class ApiWrapper<T> {

    public ApiWrapper() {
    }

    private boolean success;
    private String message;
    private T data;

    public ApiWrapper(T data, String message, boolean success) {
        this.data = data;
        this.message = message;
        this.success = success;
    }
    public ApiWrapper(String message, boolean success) {
        this(null, message, success);
    }
}
