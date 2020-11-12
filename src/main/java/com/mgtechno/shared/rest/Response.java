package com.mgtechno.shared.rest;

import java.util.List;
import java.util.Map;

public class Response {
    private int statusCode;
    private Map<String, List<String>> headers;
    private Object body;

    public Response(){

    }

    public Response(int statusCode, Map<String, List<String>> headers, String body) {
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

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }
}
