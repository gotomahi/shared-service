package com.mgtechno.shared.util;

import java.util.Collection;
import java.util.Map;

public class CollectionUtil {
    public static boolean isNotEmpty(Collection collection){
        return collection != null && collection.size() > 0;
    }

    public static boolean isNotEmpty(Map map){
        return map != null && map.size() > 0;
    }
}
