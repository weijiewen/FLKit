package com.wjw.flkit.unit;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class FLUserDefault implements Serializable {
    public static FLUserDefault userDefault = new FLUserDefault();
    public final void put(Context context, String key, Integer value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final void put(Context context, String key, Float value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final void put(Context context, String key, Double value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final void put(Context context, String key, Boolean value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final void put(Context context, String key, String value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final void put(Context context, String key, Serializable value) {
        readMap(context).put(key, value);
        writeMap(context);
    }
    public final Integer getInt(Context context, String key) {
        return (Integer) readMap(context).get(key);
    }
    public final Float getFloat(Context context, String key) {
        return (Float) readMap(context).get(key);
    }
    public final Double getDouble(Context context, String key) {
        return (Double) readMap(context).get(key);
    }
    public final Boolean getBoolean(Context context, String key) {
        return (Boolean) readMap(context).get(key);
    }
    public final String getString(Context context, String key) {
        return (String) readMap(context).get(key);
    }
    public final Serializable getSerializable(Context context, String key) {
        return (Serializable) readMap(context).get(key);
    }
    public final void remove(Context context, String key) {
        readMap(context).remove(key);
        writeMap(context);
    }
    public final HashMap getMap(Context context) {
        return (HashMap) readMap(context).clone();
    }
    public final static String getLocalString(Context context, String fileName) {
        String data = null;
        try {
            String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
            ObjectInputStream objectInputStream =
                    new ObjectInputStream( new FileInputStream( new File(path) ) );
            data = (String) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {

        }
        return data;
    }
    public final static void saveLocalString(Context context, String fileName, String data) {
        try {
            String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
            ObjectOutputStream objectOutputStream =
                    new ObjectOutputStream( new FileOutputStream(new File(path)));
            objectOutputStream.writeObject(data);
            objectOutputStream.close();
        } catch (IOException e) {

        }
    }
    private final String fileName = "FLUserDefault";
    private HashMap map;
    private FLUserDefault() {}
    private HashMap readMap(Context context) {
        if (map == null) {
            HashMap hashMap = null;
            try {
                String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
                ObjectInputStream objectInputStream =
                        new ObjectInputStream( new FileInputStream( new File(path) ) );
                hashMap = (HashMap) objectInputStream.readObject();
                objectInputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                
            }
            if (hashMap == null) {
                hashMap = new HashMap();
            }
            map = hashMap;
        }
        return map;
    }
    private void writeMap(Context context) {
        if (map != null) {
            try {
                String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
                ObjectOutputStream objectOutputStream =
                        new ObjectOutputStream( new FileOutputStream(new File(path)));
                objectOutputStream.writeObject(map);
                objectOutputStream.close();
            } catch (IOException e) {
                
            }
        }
    }
}
