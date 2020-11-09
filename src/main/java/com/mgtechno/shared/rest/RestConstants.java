package com.mgtechno.shared.rest;

public interface RestConstants {
    String BASIC = "Basic";
    String BEARER = "Bearer";
    String TAB_SPACE = " ";
    String COLON = ":";
    String PERIOD = ".";
    String PERIOD_REGEX = "\\.";
    String SECRET_KEY = "FREE_MASON"; //@TODO Add Signature here
    char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    String ISSUER = "mgtechno.co.uk";
    String JWT_HEADER = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
    String HEADER_AUTHORIZATION = "Authorization";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";
}
