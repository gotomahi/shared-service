package com.mgtechno.shared.rest;

import java.util.Map;

public class Response {
    private int statusCode;
    private Map<String, String> headers;
    private Object body;

    public Response(){

    }

    public Response(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
