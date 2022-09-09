package com.wjw.flkit.unit;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class FLRedis {
    public static FLRedis redis = new FLRedis();
    private HashMap<String, HashMap<String, Object>> map = new HashMap<>();
    private FLRedis() {}
    public static void addValue(String name, String key, Object value) {
        if (redis.map.get(name) == null) {
            redis.map.put(name, new HashMap<>());
        }
        redis.map.get(name).put(key, value);
    }
    public static void removeValue(String name, String key) {
        if (redis.map.get(name) != null) {
            redis.map.get(name).remove(key);
            if (redis.map.get(name).isEmpty()) {
                redis.map.remove(name);
            }
        }
    }
    @Nullable
    public static Integer getIntegerValue(String name, String key) {
        return (Integer) getValue(name, key);
    }
    @Nullable
    public static Double getDoubleValue(String name, String key) {
        return (Double) getValue(name, key);
    }
    @Nullable
    public static String getStringValue(String name, String key) {
        return (String) getValue(name, key);
    }
    @Nullable
    public static Object getValue(String name, String key) {
        if (redis.map.get(name) == null) {
            return null;
        }
        Object value = redis.map.get(name).get(key);
        return value;
    }
}
