package com.mgtechno.shared.rest;

import java.lang.reflect.Method;

public class PathInfo {
    private String path;
    private HttpMethod requestMethod;
    private Route route;
    private Method method;

    public PathInfo(){

    }

    public PathInfo(String path, HttpMethod requestMethod, Route route, Method method) {
        this.path = path;
        this.requestMethod = requestMethod;
        this.route = route;
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(HttpMethod requestMethod) {
        this.requestMethod = requestMethod;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
