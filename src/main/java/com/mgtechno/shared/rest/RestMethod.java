package com.mgtechno.shared.rest;

import java.lang.reflect.Method;
import java.util.List;

public class RestMethod {
    private String path;
    private HttpMethod requestMethod;
    private Route route;
    private Method method;
    private List<String> rolesAllowed;
    private List<String> rolesExcluded;

    public RestMethod(){

    }

    public RestMethod(String path, HttpMethod requestMethod, Route route, Method method, List<String> rolesAllowed,
                      List<String> rolesExcluded) {
        this.path = path;
        this.requestMethod = requestMethod;
        this.route = route;
        this.method = method;
        this.rolesAllowed = rolesAllowed;
        this.rolesExcluded = rolesExcluded;
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

    public List<String> getRolesAllowed() {
        return rolesAllowed;
    }

    public void setRolesAllowed(List<String> rolesAllowed) {
        this.rolesAllowed = rolesAllowed;
    }

    public List<String> getRolesExcluded() {
        return rolesExcluded;
    }

    public void setRolesExcluded(List<String> rolesExcluded) {
        this.rolesExcluded = rolesExcluded;
    }
}
