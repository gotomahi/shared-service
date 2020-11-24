package com.mgtechno.shared.rest;

public enum HttpMethod {
    POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELTEE"), PATCH("PATCH"), OPTIONS("OPTIONS");

    HttpMethod(String method) {
        this.method = method;
    }

    private String method;

    public String method() {
        return method;
    }
}
