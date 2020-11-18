package com.mgtechno.shared.rest;

public enum HttpMethod {
    POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELTEE"), ANY("ANY");

    HttpMethod(String method) {
        this.method = method;
    }

    private String method;

    public String method() {
        return method;
    }
}
