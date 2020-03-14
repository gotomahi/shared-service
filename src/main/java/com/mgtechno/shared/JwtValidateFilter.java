package com.mgtechno.shared;

import org.json.JSONObject;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

import static com.mgtechno.shared.SharedConstants.*;

public class JwtValidateFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String authorization = request.getHeader(AUTHORIZATION);
        int responseCode = 200;
        if (!StringUtils.isEmpty(authorization) && (authorization.startsWith(BASIC)
                || (authorization.startsWith(BEARER) && hasValidToken(authorization)))) {
            filterChain.doFilter(request, servletResponse);
        } else if(!isTokenVerified(authorization)){
            response.setStatus(441);
        }else if(isTokenExpired(authorization)){
            response.setStatus(442);
        }
    }

    private boolean hasValidToken(String authorization) {
        return isTokenVerified(authorization) && !isTokenExpired(authorization);
    }

    private boolean isTokenVerified(String token){
        boolean verified = false;
        String[] parts = token.split("\\.");
        return verified;
    }

    private boolean isTokenExpired(String token){
        boolean expired = false;
        String decodedStr = decode(token);
        String[] parts = token.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        return payload.getLong("exp") > (System.currentTimeMillis() / 1000);
    }

    private String getAuthorizeHeader(String authorization, String clientId, String secret) {
        if (StringUtils.isEmpty(authorization)) {
            authorization = BASIC + TAB_SPACE + Base64.getEncoder().encodeToString((clientId + COLON + secret).getBytes());
        }
        return authorization;
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
}
