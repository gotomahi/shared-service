package com.mgtechno.shared.rest;

import com.mgtechno.shared.util.CollectionUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.mgtechno.shared.rest.RestConstant.*;

public class RequestTemplate {
    public HttpResponse makeRequest(String url, String requestMethod, String body, Map<String, String> headers, Map<String, String> params) throws IOException {
        StringBuilder urlBuilder = new StringBuilder(url);
        urlBuilder.append(CollectionUtil.isNotEmpty(params) ? QUESTION_MARK : EMPTY_STRING);
        params.forEach((k, v) -> urlBuilder.append(k).append(EQUALS).append(v));
        return makeRequest(urlBuilder.toString(), requestMethod, body, headers);
    }

    public HttpResponse makeRequest(String url, String requestMethod, String body, Map<String, String> headers) throws IOException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(url))
                .header(HEADER_AUTHORIZATION, headers.get(HEADER_AUTHORIZATION))
                .method(requestMethod, HttpRequest.BodyPublishers.ofString(body));
        //headers.forEach((key, value) -> requestBuilder.header(key, value));
        HttpResponse response = null;
        try {
            response = client.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
        }catch (InterruptedException ie){
            ie.printStackTrace();
        }
        return response;
    }

}
