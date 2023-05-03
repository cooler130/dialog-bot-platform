package com.cooler.ai.distribution.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HttpResponse<T> {
    private int code;
    private String message;
    private T data;
}
