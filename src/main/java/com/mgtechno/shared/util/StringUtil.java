package com.mgtechno.shared.util;

import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;

public class StringUtil {
    public static boolean isEmpty(String value){
        return value == null || EMPTY_STRING.equals(value.trim());
    }
    public static String trimLastChar(String str, String matchChar){
        if(str.endsWith(matchChar)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
}
