package com.mgtechno.shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JSON {
    private static JSON json = null;
    private Gson gson;

    public static JSON getJson(){
        if(json == null){
            json = new JSON();
            json.init();
        }
        return json;
    }

    private void init(){
        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
//            public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//                return Base64.getDecoder().decode(json.getAsString());
//            }
//        });
//        builder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
//            @Override
//            public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
//                return new JsonPrimitive(Base64.getEncoder().encodeToString(src));
//            }
//        });
        gson = builder.create();
    }

    public String toJson(Object object){
        return gson.toJson(object);
    }

    public <T> T fromJson(String str, Type type){
        return gson.fromJson(str, type);
    }
}
