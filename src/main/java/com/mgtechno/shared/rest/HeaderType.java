package com.mgtechno.shared.rest;

import com.mgtechno.shared.KeyValue;

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

    public static List<KeyValue> corsHeaders(){
        List<KeyValue> cors = new ArrayList<>();
        cors.add(new KeyValue("Access-Control-Allow-Origin", "*"));
        cors.add(new KeyValue("Access-Control-Allow-Methods","GET"));
        cors.add(new KeyValue("Access-Control-Allow-Methods","POST"));
        cors.add(new KeyValue("Access-Control-Allow-Methods","PUT"));
        cors.add(new KeyValue("Access-Control-Allow-Methods","DELETE"));
        cors.add(new KeyValue("Access-Control-Allow-Methods","OPTIONS"));
        cors.add(new KeyValue("Access-Control-Allow-Headers", "Content-Type"));
        cors.add(new KeyValue("Access-Control-Allow-Headers", "Authorization"));
        cors.add(new KeyValue("Access-Control-Allow-Headers", "Host"));
        cors.add(new KeyValue("Access-Control-Allow-Headers", "Referer"));
        cors.add(new KeyValue("Host", "*"));
        cors.add(new KeyValue("Referer", "*"));
        return cors;
    }

    public static KeyValue pptHeaders() {
        return new KeyValue("Content-Type", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    }

    public static KeyValue htmlHeaders() {
        return new KeyValue("Content-Type", "text/html");
    }

    public static KeyValue contentDisposition(String filename) {
        return new KeyValue("Content-Disposition", "attchment; filename=" + filename);
    }

    public static KeyValue jsonContent() {
        return new KeyValue("Content-Type", "application/json");
    }
}
