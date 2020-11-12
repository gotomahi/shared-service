package com.mgtechno.shared.rest;

import com.mgtechno.shared.util.CollectionUtil;
import com.mgtechno.shared.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.rest.HttpStatus.SERVER_ERROR;
import static com.mgtechno.shared.rest.RestConstant.*;

/**
 *
 */
public class RequestHandler implements HttpHandler {
    private static final Logger LOG = Logger.getLogger(RequestHandler.class.getCanonicalName());
    private Map<String, Route> routes;

    public RequestHandler(Route... routes){
        this.routes = new HashMap<>();
        for(Route route: routes) {
            this.routes.put(route.path(), route);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Response response = null;
        String uriPath = exchange.getRequestURI().getPath();
        OutputStream outputStream = exchange.getResponseBody();
        try {
            Route route = routes.keySet().stream().filter(path -> isPathMatched(uriPath, path))
                    .map(path -> routes.get(path)).findFirst().get();
            response = route.process(exchange);
            sendResponse(exchange, outputStream, response);
        }catch (Exception e){
            sendResponse(exchange, outputStream, new Response(SERVER_ERROR.code(), new HashMap(), e.getMessage()));
            LOG.log(Level.SEVERE, "Failed to process request", e);
        }
    }

    private void sendResponse(HttpExchange exchange, OutputStream outputStream, Response response) throws IOException{
        if(CollectionUtil.isNotEmpty(response.getHeaders())) {
            exchange.getResponseHeaders().putAll(response.getHeaders());
        }
        String body = response.getBody() != null ? response.getBody().toString() : EMPTY_STRING;
        byte[] responseBody = body.getBytes(CHARSET_UTF8);
        exchange.sendResponseHeaders(response.getStatusCode(), responseBody.length);
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private boolean isPathMatched(String uriPath, String resourcePath){
        String[] uriPaths = uriPath.split(FORWARD_SLASH);
        String[] resourcePaths = resourcePath.split(FORWARD_SLASH);
        boolean matched = resourcePaths.length == 0 ? true : false;
        if(resourcePaths.length > 0 && resourcePaths.length <= uriPaths.length){
            matched = true;
            for(int i = 1; i < resourcePaths.length; i++){
                if(!(!StringUtil.isEmpty(uriPaths[i]) && (resourcePaths[i].equals(uriPaths[i])
                        || (resourcePaths[i].startsWith(LEFT_BRACE) && resourcePaths[i].endsWith(RIGHT_BRACE))))
                ){
                    matched = false;
                    break;
                }
            }
        }
        return matched;
    }
}
