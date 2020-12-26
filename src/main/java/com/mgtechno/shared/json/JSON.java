package com.mgtechno.shared.json;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JSON {
    private static JSON json = null;
    private Gson gson = new Gson();

    public static JSON getJson(){
        if(json == null){
            json = new JSON();
        }
        return json;
    }

    public String toJson(Object object){
        return gson.toJson(object);
    }

    public <T> T fromJson(String str, Type type){
        return gson.fromJson(str, type);
    }
}
