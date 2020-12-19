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

    public static Map<String, List<String>> corsHeaders(){
        Map<String, List<String>> respHeaders = new HashMap<>();
        List<String> acAllowOrigin = new ArrayList<>();
        acAllowOrigin.add("*");
        respHeaders.put("Access-Control-Allow-Origin", acAllowOrigin);
        List<String> acAllowMethods = new ArrayList<>();
        acAllowMethods.add("GET");
        acAllowMethods.add("POST");
        acAllowMethods.add("PUT");
        acAllowMethods.add("DELETE");
        acAllowMethods.add("OPTIONS");
        respHeaders.put("Access-Control-Allow-Methods", acAllowMethods);
        List<String> acAllowHeaders = new ArrayList<>();
        acAllowHeaders.add("Content-Type");
        acAllowHeaders.add("Authorization");
        acAllowHeaders.add("Host");
        acAllowHeaders.add("Referer");
        respHeaders.put("Access-Control-Allow-Headers", acAllowHeaders);
        List<String> hosts = new ArrayList<>();
        hosts.add("*");
        respHeaders.put("Host", hosts);
        respHeaders.put("Referer", hosts);
        return respHeaders;
    }
}
