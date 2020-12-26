package com.mgtechno.shared.rest;

import com.mgtechno.shared.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class Response {
    private int statusCode;
    private List<KeyValue> headers;
    private Object body;

    public Response(){

    }

    public Response(int statusCode, List<KeyValue> headers, Object body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public Response(int statusCode, KeyValue header, Object body) {
        this.statusCode = statusCode;
        this.headers = new ArrayList<>();
        headers.add(header);
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public List<KeyValue> getHeaders() {
        return headers;
    }

    public void setHeaders(List<KeyValue> headers) {
        this.headers = headers;
    }
}
