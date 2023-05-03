package com.cooler.ai.dm.model;

import lombok.Data;

@Data
public class HttpResult<T> {
    private int code;
    private String message;
    private T data;

    private HttpResult setResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        return this;
    }

    public HttpResult success() {
        return setResult(200, "Success", null);
    }

    public HttpResult success(T data) {
        return setResult(200, "Success", data);
    }
    
    
    
    public HttpResult fail(int code, String message) {
        return setResult(code, message, null);
    }
    
    public HttpResult fail(String message, T  data) {
        return setResult(400, message, data);
    }
    
    public HttpResult fail(int code, String message, T  data) {
        return setResult(code, message, data);
    }


    
}