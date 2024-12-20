package com.mgtechno.shared.rest;

import com.mgtechno.shared.KeyValue;
import com.mgtechno.shared.json.JSON;
import com.mgtechno.shared.util.CollectionUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.rest.HttpStatus.SERVER_ERROR;
import static com.mgtechno.shared.rest.RestConstant.CHARSET_UTF8;
import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;

/**
 *
 */
public class RequestHandler implements HttpHandler {
    private static final Logger LOG = Logger.getLogger(RequestHandler.class.getCanonicalName());
    private List<RestMethod> paths;

    public RequestHandler(String contextPath, Route... routes){
        this.paths = RestMethodHelper.prepareRestMethods(contextPath, routes);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Response response = null;
        String uriPath = exchange.getRequestURI().getPath();
        OutputStream outputStream = null;//exchange.getResponseBody();
        try {
            List<KeyValue> pathVars = new ArrayList<>();
            RestMethod restMethod = RestMethodHelper.findMatchedPath(paths, exchange, pathVars);
            if(restMethod == null){
                response = new Response(HttpStatus.NOT_FOUND.code(), HeaderType.jsonContent(), "No resource found");
            }else {
                Object[] params = new Object[pathVars.size()+1];
                int i = 0;
                params[i++] = exchange;
                for(KeyValue keyValue : pathVars){
                    Constructor constructor = restMethod.getMethod().getParameterTypes()[i]
                            .getDeclaredConstructor(new Class[]{String.class});
                    params[i++] = constructor.newInstance(keyValue.getValue());
                }
                response = (Response) restMethod.getMethod().invoke(restMethod.getRoute(), params);
            }
            sendResponse(exchange, outputStream, response);
        }catch (Exception e){
            LOG.log(Level.SEVERE, "Failed to process request", e);
            sendResponse(exchange, outputStream, new Response(SERVER_ERROR.code(), HeaderType.jsonContent(), e.getMessage()));
        }finally {
            if(outputStream != null){
                outputStream.close();
            }
        }
    }

    private void sendResponse(HttpExchange exchange, OutputStream outputStream, Response response) throws IOException{
        if(CollectionUtil.isNotEmpty(response.getHeaders())) {
            for(KeyValue kv : response.getHeaders()){
                exchange.getResponseHeaders().add(kv.getKey(), (String)kv.getValue());
            }
        }

        byte[] responseBody = null;
        String body = EMPTY_STRING;
        if(response.getBody() != null && response.getBody() instanceof String){
            body = (String)response.getBody();
            responseBody = body.getBytes(CHARSET_UTF8);
        }else if(response.getBody() instanceof byte[]){
            responseBody = (byte[])response.getBody();
        }else if(response.getBody() != null){
            body = JSON.getJson().toJson(response.getBody());
            responseBody = body.getBytes(CHARSET_UTF8);
        }else{
            responseBody = body.getBytes();
        }
        try(OutputStream bos = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(response.getStatusCode(), responseBody.length);
            bos.write(responseBody);
            bos.flush();
        }
    }
}
