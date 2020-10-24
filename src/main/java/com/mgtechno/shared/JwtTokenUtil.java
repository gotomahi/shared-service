package com.mgtechno.shared;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.Base64;

import static com.mgtechno.shared.SharedConstants.*;

public class JwtTokenUtil {

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
