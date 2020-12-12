package com.mgtechno.shared.rest;

import com.mgtechno.shared.KeyValue;
import com.mgtechno.shared.json.JsonToObjectMapper;
import com.mgtechno.shared.util.CollectionUtil;
import com.mgtechno.shared.util.StringUtil;
import com.sun.net.httpserver.HttpExchange;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.mgtechno.shared.rest.RestConstant.*;

public class RestMethodHelper {
    public static List<RestMethod> prepareRestMethods(String contextPath, Route... routes){
        List<RestMethod> paths = new ArrayList<>();
        for (Route route : routes){
            Path rootPath = route.getClass().getAnnotation(Path.class);
            for (Method method : route.getClass().getDeclaredMethods()) {
                Path path = method.getAnnotation(Path.class);
                if (path != null) {
                    StringBuilder uri = new StringBuilder(contextPath);
                    if(rootPath != null){
                        uri.append(rootPath.value());
                    }
                    uri.append(path.value());
                    List<String> rolesAllowed = new ArrayList<>();
                    List<String> rolesExcluded = new ArrayList<>();
                    HasAuthority hasAuthority = method.getAnnotation(HasAuthority.class);
                    if(hasAuthority != null){
                        rolesAllowed = Arrays.asList(hasAuthority.value().split(",", -1));
                    }
                    paths.add(new RestMethod(StringUtil.trimLastChar(uri.toString(), FORWARD_SLASH), path.method(), route, method, rolesAllowed, rolesExcluded));
                }
            }
        }
        return paths;
    }

    public static RestMethod findMatchedPath(List<RestMethod> paths, HttpExchange exchange, List<KeyValue> pathVars){
        String requestMethod = exchange.getRequestMethod();
        String uriPath = StringUtil.trimLastChar(exchange.getRequestURI().getPath(), FORWARD_SLASH);
        String authHeader = exchange.getRequestHeaders().getFirst(HEADER_AUTHORIZATION);
        RestMethod restMethod = paths.stream().filter(path -> (
                        (requestMethod.equalsIgnoreCase(path.getRequestMethod().method())
                                || requestMethod.equalsIgnoreCase(HttpMethod.OPTIONS.method())))
                        && isMethodAllowed(path, authHeader) && (FORWARD_SLASH.equals(path.getPath())
                                || isPathMatched(uriPath, path.getPath(), pathVars, authHeader)))
                .findFirst().orElse(null);
        return restMethod;
    }

    private static boolean isMethodAllowed(RestMethod restMethod, String authHeader){
        boolean allowed = true;
        if(authHeader != null && authHeader.startsWith(BEARER)) {
            JWTToken jwtToken = new JWTToken();
            JsonToObjectMapper jsonToObjectMapper = new JsonToObjectMapper();
            Map<String, Object> tokenMap = jsonToObjectMapper.convertToMap(jwtToken.decodePayload(authHeader.split(SINGLE_SPACE)[1]));
            List<String> userRoles = (List<String>)tokenMap.get("authorities");
            allowed = CollectionUtil.isEmpty(restMethod.getRolesAllowed())
                    || (CollectionUtil.isNotEmpty(userRoles)
                    && userRoles.stream().filter(role -> restMethod.getRolesAllowed().contains(role))
                        .map(role -> true).findFirst().orElse(false));
        }
        return allowed;
    }

    private static boolean isPathMatched(String uriPath, String resourcePath, List<KeyValue> pathVars, String authHeader){
        String[] uriPaths = uriPath.split(FORWARD_SLASH, -1);
        String[] resourcePaths = resourcePath.split(FORWARD_SLASH, -1);
        boolean matched = false;
        if(resourcePaths.length > 0 && resourcePaths.length <= uriPaths.length){
            matched = true;
            for(int i = 1; i < resourcePaths.length; i++){
                if(resourcePaths[i].equals(uriPaths[i])){
                    continue;
                }else if(resourcePaths[i].startsWith(LEFT_BRACE) && resourcePaths[i].endsWith(RIGHT_BRACE)){
                    String pathVariable = resourcePaths[i].substring(1, resourcePaths[i].length() - 1);
                    pathVars.add(new KeyValue(pathVariable, uriPaths[i]));
                    continue;
                }else{
                    matched = false;
                    break;
                }
            }
        }
        if(!matched){
            //clear wrong matched path variables
            pathVars.clear();
        }
        return matched;
    }
}
