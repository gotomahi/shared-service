package com.mgtechno.shared.rest;

public interface RestConstant {
    String CHARSET_UTF8 = "UTF-8";
    String FORWARD_SLASH = "/";
    String LEFT_BRACE = "{";
    String RIGHT_BRACE = "}";
    String DOT = ".";
    String REGEX_DOT = "\\.";
    String HMACSHA256 = "HmacSHA256";
    String QUESTION_MARK = "?";
    String REGEX_QUESTION_MARK = "\\?";
    String EMPTY_STRING = "";
    String SINGLE_SPACE = " ";
    String EQUALS = "=";
    String AMPERSAND = "&";
    String FILE_DB_PROPERTIES = "db.properties";
    String DB_MIN_POOL = "db.minPool";
    String DB_MAX_POOL = "db.maxPool";
    String DB_URL = "db.url";
    String DB_USER = "db.user";
    String DB_PASSWORD = "db.password";
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
    String GRANT_TYPE = "grant_type";
    String AUTHORIZATION_CODE = "authorization_code";
    String PASSWORD = "password";
}
