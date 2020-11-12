package com.mgtechno.shared.json;

import com.mgtechno.shared.util.StringUtil;

import javax.json.*;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JsonUtil {
    public static String getFieldStringValue(String str, String property){
        String value = null;
        JsonObject jsonObject = getJsonObject(str);
        if(jsonObject != null) {
            value = jsonObject.getString(property);
        }
        return value;
    }

    private static JsonObject getJsonObject(String str){
        JsonObject jsonObject = null;
        if(!StringUtil.isEmpty(str)) {
            JsonReader jsonString = Json.createReader(new StringReader(str));
            jsonObject = jsonString.readObject();
        }
        return jsonObject;
    }

    public static String convertToJson(Map<String, Object> data){
        return createJsonObject(data).build().toString();
    }

    private static JsonObjectBuilder createJsonObject(Map<String, Object> data){
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        for(String key: data.keySet()){
            if(data.get(key) instanceof Integer) {
                jsonBuilder.add(key, (Integer) data.get(key));
            }else if(data.get(key) instanceof Long){
                jsonBuilder.add(key, (Long) data.get(key));
            }else if(data.get(key) instanceof String){
                jsonBuilder.add(key, (String) data.get(key));
            }else if(data.get(key) instanceof BigDecimal){
                jsonBuilder.add(key, (BigDecimal) data.get(key));
            }else if(data.get(key) instanceof Map){
                jsonBuilder.add(key, createJsonObject((Map)data.get(key)));
            }else if(data.get(key) instanceof List){
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for(Object obj : (List)data.get(key)) {
                    if(obj instanceof Integer) {
                        arrayBuilder.add((Integer) obj);
                    }else if(obj instanceof Long){
                        arrayBuilder.add((Integer) obj);
                    }else if(obj instanceof String){
                        arrayBuilder.add((String) obj);
                    }else if(obj instanceof BigDecimal){
                        arrayBuilder.add((BigDecimal) obj);
                    }else if(obj instanceof Long){
                        arrayBuilder.add((Long) obj);
                    }else if(obj instanceof Map){
                        arrayBuilder.add(createJsonObject((Map)obj));
                    }
                }
                jsonBuilder.add(key, arrayBuilder);
            }
        }
        return jsonBuilder;
    }
}
