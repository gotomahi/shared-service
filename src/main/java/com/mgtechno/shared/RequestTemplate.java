package com.mgtechno.shared;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.Map;

public class RequestTemplate {
    private static final String AUTHORIZATION = "Authorization";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String USER_AGENT = "User-Agent";
    static HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public String execute(Request request, Response response, String url) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        GenericUrl genericUrl = new GenericUrl(url);
        HttpContent content = ByteArrayContent.fromString(request.contentType(), request.body());
        HttpRequest httpRequest = requestFactory.buildRequest(request.requestMethod(), genericUrl, content);

        //Set incoming headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization(request.headers(AUTHORIZATION));
        headers.setContentType(request.headers(CONTENT_TYPE));
        headers.setUserAgent(request.headers(USER_AGENT));
        httpRequest.setHeaders(headers);

        //Set query parameters
        Map<String, String[]> paramMap = request.queryMap().toMap();
        for(String param : paramMap.keySet()){
            genericUrl.put(param, paramMap.get(param)[0]);
        }

        HttpResponse httpResponse = httpRequest.execute();
        response.status(httpResponse.getStatusCode());
        return httpResponse.parseAsString();
    }

    public HttpResponse execute(String url, String requestMethod, String requestContentType, String bodyContent,
                          Map<String, String> headers, Map<String, Object> queryParams) throws IOException {
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        GenericUrl genericUrl = new GenericUrl(url);
        HttpContent content = ByteArrayContent.fromString(requestContentType, bodyContent);
        HttpRequest httpRequest = requestFactory.buildRequest(requestMethod, genericUrl, content);
        HttpResponse httpResponse = httpRequest.execute();
        return httpResponse;
    }
}
