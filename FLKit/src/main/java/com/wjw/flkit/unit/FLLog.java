package com.wjw.flkit.unit;

import android.util.Log;

import com.wjw.flkit.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FLLog {
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static void logMap(HashMap<String, Object> map) {
        String string = null;
        try {
            JSONObject jsonObject = new JSONObject(map);
            string = jsonObject.toString(4);
        } catch (JSONException e) {
            
        }
        if (string == null) {
            string = "null";
        }
        else {
            string = string.replace("\\/", "/");
        }
        Log.d("network", "一一一一一一一一一一一一一一一一一一一一一一 开始 一一一一一一一一一一一一一一一一一一一一一一");
        String[] lines = string.split(LINE_SEPARATOR);
        for (int i = 0; i < lines.length; i ++) {
            Log.d("network", "|" + lines[i]);
        }
        Log.d("network", "一一一一一一一一一一一一一一一一一一一一一一 结束 一一一一一一一一一一一一一一一一一一一一一一");
    }
}
