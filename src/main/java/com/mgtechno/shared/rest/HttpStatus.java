package com.mgtechno.shared.rest;

public enum HttpStatus {
    SUCCESS(200), UNAUTHORIZED(401), NOT_FOUND(404), SERVER_ERROR(500);

    HttpStatus(int code) {
        this.code = code;
    }

    int code;

    public int code() {
        return code;
    }
}
