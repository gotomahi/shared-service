package com.mgtechno.shared;

import org.json.JSONObject;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.Base64;

import static com.mgtechno.shared.SharedConstants.*;
import static com.mgtechno.shared.SharedConstants.COLON;

public class JwtTokenUtil {

    public static boolean isTokenVerified(KeyPair keyPair, String authHeader){
        String[] parts = authHeader.split(PERIOD_REGEX);
        RsaSigner signer = new RsaSigner((RSAPrivateKey)keyPair.getPrivate());
        String encodedToken = JwtHelper.encode(decode(parts[1]), signer).getEncoded();
        boolean verified = parts[2].equals(encodedToken.split(PERIOD_REGEX)[2]);
        return verified;
    }

    public static boolean isTokenExpired(String authHeader){
        boolean expired = false;
        String[] parts = authHeader.split(PERIOD_REGEX);
        JSONObject payload = new JSONObject(decode(parts[1]));
        expired = payload.getLong("exp") < (System.currentTimeMillis() / 1000);
        return expired;
    }

    public static String getAuthorizeHeader(String authorization, String clientId, String secret) {
        if (StringUtils.isEmpty(authorization)) {
            authorization = BASIC + TAB_SPACE + Base64.getEncoder().encodeToString((clientId + COLON + secret).getBytes());
        }
        return authorization;
    }

    public static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    public static JSONObject getTokenPayload(String authHeader){
        String[] parts = authHeader.split(PERIOD_REGEX);
        JSONObject payload = new JSONObject(decode(parts[1]));
        return payload;
    }

    public static Long getUserId(String authHeader){
        return getTokenPayload(authHeader).getLong("userId");
    }

    public static Long getCustomerId(String authHeader){
        return getTokenPayload(authHeader).getLong("customerId");
    }
}
