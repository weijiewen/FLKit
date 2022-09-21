package com.wjw.flkit.unit;

import androidx.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FLRedis {
    public static FLRedis redis = new FLRedis();
    private HashMap<String, HashMap<Object, FLRedisValue>> map = new HashMap<>();
    private FLRedis() {}
    public static <V extends Object> void addValue(Class cls, Object key, V value) {
        addValue(cls.getName(), key, value);
    }
    public static <V extends Object> void addValue(String name, Object key, V value) {
        HashMap<Object, FLRedisValue> map = redis.map.get(name);
        if (map == null) {
            map = new HashMap();
            redis.map.put(name, map);
        }
        FLRedisValue redisValue = map.get(key);
        if (redisValue == null) {
            redisValue = new FLRedisValue(name, key);
            map.put(key, redisValue);
        }
        redisValue.setValue(value);
    }
    public static void removeValue(Class cls, Object key) {
        removeValue(cls.getName(), key);
    }
    public static void removeValue(String name, Object key) {
        HashMap<Object, FLRedisValue> map = redis.map.get(name);
        if (map != null) {
            map.remove(key);
            if (map.isEmpty()) {
                redis.map.remove(name);
            }
        }
    }
    @Nullable
    public static <V extends Object> V getValue(Class cls, Object key) {
        return getValue(cls.getName(), key);
    }
    @Nullable
    public static <V extends Object> V getValue(String name, Object key) {
        HashMap<Object, FLRedisValue> map = redis.map.get(name);
        if (map == null) {
            return null;
        }
        FLRedisValue value = map.get(key);
        if (value == null) {
            return null;
        }
        return (V) value.value;
    }
    public static void addListener(Class cls, Object key, FLRedisListener listener) {
        addListener(cls.getName(), key, listener);
    }
    public static void addListener(String name, Object key, FLRedisListener listener) {
        HashMap<Object, FLRedisValue> map = redis.map.get(name);
        if (map == null) {
            map = new HashMap<>();
            redis.map.put(name, map);
        }
        FLRedisValue value = map.get(key);
        if (value == null) {
            value = new FLRedisValue(name, key);
            map.put(key, value);
        }
        value.list.add(new WeakReference<>(listener));
    }
    public interface FLRedisListener<K extends Object, V extends Object> {
        void redisValueChange(String name, K key, V value);
    }
    private static class FLRedisValue {
        private String name;
        private Object key;
        private Object value;
        private List<WeakReference<FLRedisListener>> list = new ArrayList<>();
        public FLRedisValue(String name, Object key) {
            this.name = name;
            this.key = key;
        }
        public void setValue(Object value) {
            this.value = value;
            List<WeakReference<FLRedisListener>> list = new ArrayList<>();
            list.addAll(this.list);
            for (WeakReference<FLRedisListener> reference: list) {
                if (reference.get() == null) {
                    this.list.remove(reference);
                }
                else {
                    reference.get().redisValueChange(name, key, value);
                }
            }
        }
    }
}
