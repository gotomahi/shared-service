package com.mgtechno.shared.rest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.mgtechno.shared.rest.RestConstant.*;
import static com.mgtechno.shared.rest.RestConstants.*;

public class JWTToken {
    private static final Logger LOG = Logger.getLogger(JWTToken.class.getCanonicalName());

    public JWTToken() {
    }

    public String createToken(String payload) {
        String encodedHeader = encode(JWT_HEADER);
        String signature = hmacSha256(encodedHeader + DOT + encode(payload), SECRET_KEY);
        return encodedHeader + DOT + encode(payload) + DOT + signature;
    }

    public void validateToken(String token) throws Exception {
        String encodedHeader = encode(JWT_HEADER);
        String[] parts = token.split(REGEX_DOT);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid Token format");
        }
        if (encodedHeader.equals(parts[0])) {
            encodedHeader = parts[0];
        } else {
            throw new NoSuchAlgorithmException("JWT Header is Incorrect: " + parts[0]);
        }

        String payload = decode(parts[1]);
        if (payload.isEmpty()) {
            throw new Exception("Payload is Empty: ");
        }
        String signature = parts[2];
        boolean valid = signature.equals(hmacSha256(encodedHeader + "." + encode(payload), SECRET_KEY));
        if(!valid){
            throw new Exception("Token has been tampered.");
        }
    }

    private static String encode(String obj) {
        return encode(obj.getBytes(StandardCharsets.UTF_8));
    }

    private static String encode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String decode(String encodedString) {
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }

    /**
     * Sign with HMAC SHA256 (HS256)
     *
     * @param data
     * @return
     * @throws Exception
     */
    private String hmacSha256(String data, String secret) {
        try {
            byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
            Mac sha256Hmac = Mac.getInstance(HMACSHA256);
            SecretKeySpec secretKey = new SecretKeySpec(hash, HMACSHA256);
            sha256Hmac.init(secretKey);

            byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return encode(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }
    }

}