package com.mgtechno.shared.rest;

import com.google.gson.reflect.TypeToken;
import com.mgtechno.shared.entity.Token;
import com.mgtechno.shared.json.JSON;
import com.mgtechno.shared.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mgtechno.shared.rest.RestConstant.*;

public interface Route {

    default Map<String, String> requestParams(HttpExchange exchange){
        Map<String, String> params = new HashMap<>();
        String[] uriParts = exchange.getRequestURI().toString().split(REGEX_QUESTION_MARK, -1);
        if(uriParts.length > 1) {
            if (!StringUtil.isEmpty(uriParts[1])) {
                String[] queryParams = uriParts[1].split(AMPERSAND);
                for (String queryParam : queryParams) {
                    String[] parts = queryParam.split(EQUALS);
                    params.put(parts[0], parts[1]);
                }
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
        String body = null;
        try(BufferedInputStream bis = new BufferedInputStream(exchange.getRequestBody())) {
            byte[] data = bis.readAllBytes();
            body = new String(data, CHARSET_UTF8);
        }
        return body;
    }

    default Long getCustomerId(HttpExchange exchange){
        return Long.parseLong(getAuthPayloadProperty(exchange).getCustomerId().toString());
    }

    default Long getUserId(HttpExchange exchange){
        return Long.parseLong(getAuthPayloadProperty(exchange).getUserId().toString());
    }

    default Token getAuthPayloadProperty(HttpExchange exchange){
        Map<String, String> headers = headers(exchange);
        String authPayload = JWTToken.getJwtToken().decodePayload(headers.get(HEADER_AUTHORIZATION));
        Token token = JSON.getJson().fromJson(authPayload, new TypeToken<Token>(){}.getType());
        return token;
    }
    default Response response(Object object){
        return new Response(HttpStatus.SUCCESS.code(), HeaderType.jsonContent(), object);
    }
}
