package com.mgtechno.shared.rest;

import com.mgtechno.shared.KeyValue;
import com.mgtechno.shared.json.ObjectToJsonMapper;
import com.mgtechno.shared.util.CollectionUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.rest.HttpStatus.SERVER_ERROR;
import static com.mgtechno.shared.rest.RestConstant.*;

/**
 *
 */
public class RequestHandler implements HttpHandler {
    private static final Logger LOG = Logger.getLogger(RequestHandler.class.getCanonicalName());
    private List<RestMethod> paths;
    private ObjectToJsonMapper jsonMapper;

    public RequestHandler(String contextPath, Route... routes){
        this.jsonMapper = new ObjectToJsonMapper();
        this.paths = RestMethodHelper.prepareRestMethods(contextPath, routes);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Response response = null;
        String uriPath = exchange.getRequestURI().getPath();
        OutputStream outputStream = exchange.getResponseBody();
        try {
            List<KeyValue> pathVars = new ArrayList<>();
            RestMethod restMethod = RestMethodHelper.findMatchedPath(paths, exchange, pathVars);
            if(restMethod == null){
                response = new Response(HttpStatus.NOT_FOUND.code(),
                        HeaderType.addHeader(null, HeaderType.JSON_CONTENT), "No resource found");
            }else if(pathVars.isEmpty()) {
                response = (Response) restMethod.getMethod().invoke(restMethod.getRoute(), exchange);
            }else {
                Object[] params = new Object[pathVars.size()];
                int i = 0;
                for(KeyValue keyValue : pathVars){
                    params[i] = keyValue.getValue();
                }
                if(restMethod.getMethod().getParameterCount() == 2) {
                    response = (Response) restMethod.getMethod().invoke(restMethod.getRoute(), exchange, params[0]);
                }else if(restMethod.getMethod().getParameterCount() == 3) {
                    response = (Response) restMethod.getMethod().invoke(restMethod.getRoute(), exchange, params[0], params[1]);
                }else if(restMethod.getMethod().getParameterCount() == 4) {
                    response = (Response) restMethod.getMethod().invoke(restMethod.getRoute(), exchange, params[0], params[1], params[2]);
                }
            }
            sendResponse(exchange, outputStream, response);
        }catch (Exception e){
            sendResponse(exchange, outputStream, new Response(SERVER_ERROR.code(), new HashMap(), e.getMessage()));
            LOG.log(Level.SEVERE, "Failed to process request", e);
        }finally {
            if(outputStream != null){
                outputStream.close();
            }
        }
    }

    private void sendResponse(HttpExchange exchange, OutputStream outputStream, Response response) throws IOException{
        if(CollectionUtil.isNotEmpty(response.getHeaders())) {
            exchange.getResponseHeaders().putAll(response.getHeaders());
        }

        String body = EMPTY_STRING;
        if(response.getBody() != null && response.getBody() instanceof String){
            body = (String)response.getBody();
        }else if(response.getBody() != null){
            body = jsonMapper.toJson(response.getBody());
        }

        byte[] responseBody = body.getBytes(CHARSET_UTF8);

        exchange.sendResponseHeaders(response.getStatusCode(), responseBody.length);
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
