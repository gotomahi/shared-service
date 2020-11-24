package com.mgtechno.shared.json;

import java.util.Map;
import java.util.logging.Logger;

public class JsonUtil {
    private static final Logger LOG = Logger.getLogger(JsonUtil.class.getCanonicalName());

    public static Object getPropertyValue(String jsonString, String property){
        JsonToObjectMapper objectMapper = new JsonToObjectMapper();
        Map<String, Object> data = objectMapper.convertToMap(jsonString);
        return data.get(property);
    }
}
