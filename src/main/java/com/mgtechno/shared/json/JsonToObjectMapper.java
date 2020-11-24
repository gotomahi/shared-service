package com.mgtechno.shared.json;

import com.mgtechno.shared.util.StringUtil;

import javax.json.*;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class JsonToObjectMapper {
    private static final Logger LOG = Logger.getLogger(JsonToObjectMapper.class.getCanonicalName());

    public String getFieldStringValue(String str, String property){
        String value = null;
        JsonObject jsonObject = getJsonObject(str);
        if(jsonObject != null) {
            value = jsonObject.getString(property);
        }
        return value;
    }

    public Map<String, Object> convertToMap(String str){
        Map<String, Object> map = new HashMap<>();
        JsonObject json = getJsonObject(str);
        for(Map.Entry<String, JsonValue> entry : json.entrySet()){
            if(entry.getValue().getValueType().equals(JsonValue.ValueType.STRING)){
                map.put(entry.getKey(), json.getString(entry.getKey()));
            }else if(entry.getValue().getValueType().equals(JsonValue.ValueType.NUMBER)){
                map.put(entry.getKey(), json.getJsonNumber(entry.getKey()).numberValue());
            }
        }
        return map;
    }

    public <T> T toObject(String str, Class<T> clazz) throws Exception {
        JsonObject json = getJsonObject(str);
        return toObject(json, clazz);
    }

    private <T> T toObject(JsonObject json, Class<T> clazz) throws Exception {
        T object = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            field.setAccessible(true);
            if(field.getType().equals(Long.class) && json.getJsonNumber(field.getName()) != null){
                field.set(object, json.getJsonNumber(field.getName()).longValue());
            }else if(field.getType().equals(Integer.class)){
                field.set(object, json.getJsonNumber(field.getName()).intValue());
            }else if(field.getType().equals(String.class)){
                field.set(object, json.getString(field.getName(), null));
            }else if(field.getType().equals(Set.class) && json.getJsonArray(field.getName()) != null) {
                Set set = new HashSet();
                JsonArray jsonArray = json.getJsonArray(field.getName());
                String genericType = ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0].getTypeName();
                for(int i = 0; i < jsonArray.size(); i++){
                    set.add(toObject(jsonArray.getJsonObject(i), Class.forName(genericType)));
                }
                field.set(object, set);
            }else if(field.getType().getPackageName().startsWith("com.mgtechno")){
                field.set(object, toObject(json.getJsonObject(field.getName()), field.getType()));
            }
        }
        return object;
    }

    private JsonObject getJsonObject(String str){
        JsonObject jsonObject = null;
        if(!StringUtil.isEmpty(str)) {
            JsonReader jsonString = Json.createReader(new StringReader(str));
            jsonObject = jsonString.readObject();
        }
        return jsonObject;
    }
}
