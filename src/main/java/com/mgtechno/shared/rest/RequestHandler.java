package com.mgtechno.shared.rest;

import com.mgtechno.shared.util.CollectionUtil;
import com.mgtechno.shared.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
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
    private Map<String, PathInfo> pathMap;

    public RequestHandler(Route... routes){
        this.pathMap = new HashMap<>();
        Path rootPath = routes.getClass().getAnnotation(Path.class);
        for (Route route : routes){
            for (Method method : route.getClass().getDeclaredMethods()) {
                Path path = method.getAnnotation(Path.class);
                if (path != null) {
                    StringBuilder uri = new StringBuilder();
                    if(rootPath != null){
                        uri.append(rootPath.value());
                    }
                    uri.append(path.value());
                    pathMap.put(uri.toString(), new PathInfo(path.value(), path.method(), route, method));
                }
            }
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Response response = null;
        String uriPath = exchange.getRequestURI().getPath();
        OutputStream outputStream = exchange.getResponseBody();
        try {
            PathInfo pathInfo = pathMap.keySet().stream().filter(path -> isPathMatched(uriPath, path))
                    .map(path -> pathMap.get(path)).findFirst().get();
            response = (Response) pathInfo.getMethod().invoke(pathInfo.getRoute(), exchange);
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
