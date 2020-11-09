package com.mgtechno.shared.util;

import static com.mgtechno.shared.rest.RestConstant.EMPTY_STRING;

public class StringUtil {
    public static boolean isEmpty(String value){
        return value == null || EMPTY_STRING.equals(value.trim());
    }
}
