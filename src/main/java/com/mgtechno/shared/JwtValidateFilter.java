package com.mgtechno.shared;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyPair;

import static com.mgtechno.shared.JwtTokenUtil.isTokenExpired;
import static com.mgtechno.shared.JwtTokenUtil.isTokenVerified;
import static com.mgtechno.shared.SharedConstants.*;

public class JwtValidateFilter extends GenericFilterBean {
    private KeyPair keyPair;

    public JwtValidateFilter(String keyStorePath, String password, String alias){
        this.keyPair = new KeyStoreKeyFactory(new ClassPathResource(keyStorePath), password.toCharArray()).getKeyPair(alias);
    }

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
        } else if(!isTokenVerified(keyPair, authorization)){
            response.setStatus(441);
        }else if(isTokenExpired(authorization)){
            response.setStatus(442);
        }
    }

    private boolean hasValidToken(String authorization) {
        return isTokenVerified(keyPair, authorization) && !isTokenExpired(authorization);
    }
}
