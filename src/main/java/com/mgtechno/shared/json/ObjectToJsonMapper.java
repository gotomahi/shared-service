package com.mgtechno.shared.json;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectToJsonMapper {
    private static final Logger LOG = Logger.getLogger(ObjectToJsonMapper.class.getCanonicalName());

    public String toJson(Object object){
        String jsonString = null;
        try{
            if(object instanceof Collection){
                jsonString = toJsonArray((Collection) object).build().toString();
            }else{
                jsonString = toJsonObject(object).build().toString();
            }
        }catch (Exception e){
            LOG.log(Level.SEVERE, "Failed to convert object to json string", e);
        }
        return jsonString;
    }

    private JsonObjectBuilder toJsonObject(Object object) throws Exception {
        JsonObjectBuilder objBuilder = Json.createObjectBuilder();
        if(object instanceof Map){
            for(String key : ((Map<String, Object>)object).keySet()){
                addJsonField(objBuilder, key, ((Map)object).get(key));
            }
        }else {
            for(Field field : object.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if(field.get(object) instanceof Collection){
                    objBuilder.add(field.getName(), toJsonArray((Collection)field.get(object)));
                }else {
                    addJsonField(objBuilder, field.getName(), field.get(object));
                }
            }

        }
        return objBuilder;
    }

    private JsonArrayBuilder toJsonArray(Collection collection)throws Exception{
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for(Object obj : collection){
            if(obj.getClass().isPrimitive()){
                addJsonField(arrayBuilder, obj);
            }else{
                arrayBuilder.add(toJsonObject(obj));
            }
        }
        return arrayBuilder;
    }

    private void addJsonField(JsonObjectBuilder objBuilder, String name, Object value){
        if(value instanceof Integer) {
            objBuilder.add(name, (Integer) value);
        }else if(value instanceof Long){
            objBuilder.add(name, (Long) value);
        }else if(value instanceof String){
            objBuilder.add(name, (String) value);
        }else if(value instanceof BigDecimal){
            objBuilder.add(name, (BigDecimal) value);
        }else if(value instanceof Boolean){
            objBuilder.add(name, (Boolean) value);
        }
    }

    private void addJsonField(JsonArrayBuilder arrayBuilder, Object value){
        if(value instanceof Integer) {
            arrayBuilder.add((Integer) value);
        }else if(value instanceof Long){
            arrayBuilder.add((Long) value);
        }else if(value instanceof String){
            arrayBuilder.add((String) value);
        }else if(value instanceof BigDecimal){
            arrayBuilder.add((BigDecimal) value);
        }
    }
}
