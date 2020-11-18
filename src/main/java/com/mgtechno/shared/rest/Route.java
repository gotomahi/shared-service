package com.mgtechno.shared.rest;

import com.mgtechno.shared.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mgtechno.shared.rest.RestConstant.*;

public interface Route {

    default Map<String, String> requestParams(HttpExchange exchange){
        Map<String, String> params = new HashMap<>();
        String queryString = exchange.getRequestURI().toString().split(REGEX_QUESTION_MARK)[1];
        if(!StringUtil.isEmpty(queryString)){
            String[] queryParams = queryString.split(AMPERSAND);
            for(String queryParam: queryParams){
                String[] parts = queryParam.split(EQUALS);
                params.put(parts[0], parts[1]);
            }
        }
        return params;
    }
    default Map<String, String> headers(HttpExchange exchange){
        Map<String, String> headers = new HashMap<>();
        Set<String> headerKeys = exchange.getRequestHeaders().keySet();
        for(String key: headerKeys){
            //capturing first header value
            headers.put(key, exchange.getRequestHeaders().getFirst(key));
        }
        return headers;
    }

    default String body(HttpExchange exchange) throws IOException {
        byte[] data = new byte[exchange.getRequestBody().available()];
        exchange.getRequestBody().read(data);
        String body = new String(data, CHARSET_UTF8);
        return body;
    }
}
