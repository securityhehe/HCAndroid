package com.hc.uicomponent.utils;


import android.text.TextUtils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GsonUtils {


    public static String getNoteJsonString(String jsonString, String note) {//
        if (TextUtils.isEmpty(jsonString)) {
            throw new RuntimeException("json字符串");
        }
        if (TextUtils.isEmpty(note)) {
            throw new RuntimeException("note标签不能为空");
        }
        JsonElement element = new JsonParser().parse(jsonString);
        if (element.isJsonNull()) {
            throw new RuntimeException("得到的jsonElement对象为空");
        }
        return element.getAsJsonObject().get(note).toString();
    }


    public static <T> List<T> parserJsonToArrayBeans(String jsonString, String note, Class<T> beanClazz) {//
        String noteJsonString = getNoteJsonString(jsonString, note);
        return parserJsonToArrayBeans(noteJsonString, beanClazz);
    }


    public static <T> List<T> parserJsonToArrayBeans(String jsonString, Class<T> beanClazz) {//
        if (TextUtils.isEmpty(jsonString)) {
            throw new RuntimeException("json字符串为空");
        }
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        if (jsonElement.isJsonNull()) {
            throw new RuntimeException("得到的jsonElement对象为空");
        }
        if (!jsonElement.isJsonArray()) {
            throw new RuntimeException("json字符不是一个数组对象集合");
        }
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<T> beans = new ArrayList<T>();
        for (JsonElement jsonElement2 : jsonArray) {
            T bean = new Gson().fromJson(jsonElement2, beanClazz);
            beans.add(bean);
        }
        return beans;
    }


    public static <T> T parserJsonToArrayBean(String jsonString, Class<T> clazzBean) {//
        if (TextUtils.isEmpty(jsonString)) {
            throw new RuntimeException("json字符串为空");
        }
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        if (jsonElement.isJsonNull()) {
            throw new RuntimeException("json字符串为空");
        }
        if (!jsonElement.isJsonObject()) {
            throw new RuntimeException("json不是一个对象");
        }
        return new Gson().fromJson(jsonElement, clazzBean);
    }


    public static <T> T parserJsonToArrayBean(String jsonString, String note, Class<T> clazzBean) {//
        String noteJsonString = getNoteJsonString(jsonString, note);
        return parserJsonToArrayBean(noteJsonString, clazzBean);
    }

    public static String toJsonString(Object obj) {//
        try {
            if (obj != null) {
                return new Gson().toJson(obj);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    public static <K, V> Map<K, V> parseJsonToMap(String json) {//
        Map<K, V> map = new Gson().fromJson(json, new TypeToken<Map<K, V>>() {
        }.getType());
        return map;
    }


    public static <T> T jsonString2Bean(String jsonString, Class<T> beanClazz) {
        try {
            if (jsonString == null) {
                return null;
            }
            T object = new Gson().fromJson(jsonString, beanClazz);
            return object;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> T jsonByTypeAdapter(String json, Class<T> clazz) {
        try {
            StringReader reader = new StringReader(json);
            JsonReader jsonReader = new Gson().newJsonReader(reader);
            TypeAdapter<T> adapter = new Gson().getAdapter(TypeToken.get(clazz));
            return adapter.read(jsonReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> String exclusionStrategies2Json(T t, final Class ignoreClazz,final String fieldName) {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if (fieldName != null) {
                    return f.getName().equals(fieldName);
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                if (ignoreClazz != null) {
                    return ignoreClazz == clazz;
                }
                return false;
            }
        });
        Gson gson = builder.create();
        String jsonResult = gson.toJson(t);
        return jsonResult;
    }

    public static <T> T exclusionStrategies2Bean(String json, Class<T> t, final Class ignoreClazz,final String fieldName) {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if (fieldName != null) {
                    return f.getName().equals(fieldName);
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                if (ignoreClazz != null) {
                    return ignoreClazz == clazz;
                }
                return false;
            }
        });
        Gson gson = builder.create();
        return gson.fromJson(json, t);
    }

    public static <T> String simpleExclusionStrategies2Json(String json, T t, final Class ignoreClazz) {
        GsonBuilder builder = new GsonBuilder();
        builder.setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                if (ignoreClazz != null) {
                    return ignoreClazz == clazz;
                }
                return false;
            }
        });
        Gson gson = builder.create();
        return gson.toJson(t);
    }

}
