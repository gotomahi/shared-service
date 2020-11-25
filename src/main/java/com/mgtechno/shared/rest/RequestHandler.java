package com.mgtechno.shared.rest;

import com.mgtechno.shared.json.ObjectToJsonMapper;
import com.mgtechno.shared.util.CollectionUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private List<PathInfo> paths;
    private ObjectToJsonMapper jsonMapper;

    public RequestHandler(Route... routes){
        this.jsonMapper = new ObjectToJsonMapper();
        this.paths = new ArrayList<>();
        for (Route route : routes){
            Path rootPath = route.getClass().getAnnotation(Path.class);
            for (Method method : route.getClass().getDeclaredMethods()) {
                Path path = method.getAnnotation(Path.class);
                if (path != null) {
                    StringBuilder uri = new StringBuilder();
                    if(rootPath != null){
                        uri.append(rootPath.value());
                    }
                    uri.append(path.value());
                    paths.add(new PathInfo(uri.toString(), path.method(), route, method));
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
            Map<String, String> pathVarMap = new HashMap<>();
            PathInfo pathInfo = paths.stream()
                    .filter(path -> exchange.getRequestMethod().equalsIgnoreCase(path.getRequestMethod().method())
                                && (FORWARD_SLASH.equals(path.getPath()) || isPathMatched(uriPath, path.getPath(), pathVarMap)))
                    .findFirst().orElse(null);
            if(pathInfo == null){
                response = new Response(HttpStatus.NOT_FOUND.code(),
                        HeaderType.addHeader(null, HeaderType.JSON_CONTENT), "No resource found");
            }else if(pathInfo.getMethod().getParameterCount() == 1) {
                response = (Response) pathInfo.getMethod().invoke(pathInfo.getRoute(), exchange);
            }else if(pathInfo.getMethod().getParameterCount() == 2){
                response = (Response)pathInfo.getMethod().invoke(pathInfo.getRoute(), exchange, pathVarMap);
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

    private boolean isPathMatched(String uriPath, String resourcePath, Map<String, String> pathVarMap){
        String[] uriPaths = uriPath.split(FORWARD_SLASH, -1);
        String[] resourcePaths = resourcePath.split(FORWARD_SLASH, -1);
        Map<String, String> pathVariableMap = new HashMap<>();
        boolean matched = false;
        if(resourcePaths.length > 0 && resourcePaths.length <= uriPaths.length){
            matched = true;
            for(int i = 1; i < resourcePaths.length; i++){
                if(resourcePaths[i].equals(uriPaths[i + 1])){
                    continue;
                }else if(resourcePaths[i].startsWith(LEFT_BRACE) && resourcePaths[i].endsWith(RIGHT_BRACE)){
                    String pathVariable = resourcePaths[i].substring(1, resourcePaths[i].length() - 1);
                    pathVariableMap.put(pathVariable, uriPaths[i + 1]);
                    continue;
                }else{
                    matched = false;
                    break;
                }
            }
        }
        if(matched && !pathVariableMap.isEmpty()){
            pathVarMap.putAll(pathVariableMap);
        }
        return matched;
    }
}
