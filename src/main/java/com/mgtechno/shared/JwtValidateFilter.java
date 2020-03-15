package com.mgtechno.shared;

import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
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
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

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
        String[] parts = token.split(PERIOD_REGEX);
        RsaSigner signer = new RsaSigner((RSAPrivateKey)this.keyPair.getPrivate());
        String encodedToken = JwtHelper.encode(decode(parts[1]), signer).getEncoded();
        boolean verified = parts[2].equals(encodedToken.split(PERIOD_REGEX)[2]);
        return verified;
    }

    private boolean isTokenExpired(String token){
        boolean expired = false;
        String[] parts = token.split(PERIOD_REGEX);
        JSONObject payload = new JSONObject(decode(parts[1]));
        expired = payload.getLong("exp") < (System.currentTimeMillis() / 1000);
        return expired;
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
