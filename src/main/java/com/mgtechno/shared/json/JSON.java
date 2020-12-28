package com.mgtechno.shared.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.logging.Logger;

public class JSON {
    private static final Logger LOG = Logger.getLogger(JSON.class.getName());
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
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        gson = builder.create();
    }

    public String toJson(Object object){
        return gson.toJson(object);
    }

    public <T> T fromJson(String str, Type type){
        return gson.fromJson(str, type);
    }
}
