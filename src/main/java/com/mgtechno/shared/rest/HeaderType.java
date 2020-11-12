package com.mgtechno.shared.rest;

import java.util.*;

public enum HeaderType {
    JSON_CONTENT("Content-Type", "application/json");

    private String type;
    private String[] values;
    HeaderType(String type, String... values){
        this.type = type;
        this.values = values;
    }
    public String headerType(){
        return type;
    }

    public List<String> headerValues(){
        return Arrays.asList(values);
    }

    public static Map<String, List<String>> addHeader(Map<String, List<String>> headers, HeaderType header){
        if(headers == null){
            headers = new HashMap<>();
        }
        headers.put(header.headerType(), header.headerValues());
        return headers;
    }
}
