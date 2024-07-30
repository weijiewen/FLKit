package com.wjw.flkit.unit;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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
        String finalString = string;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d("fllog", "一一一一一一一一一一一一一一一一一一一一一一 开始 一一一一一一一一一一一一一一一一一一一一一一");
                String[] lines = finalString.split(LINE_SEPARATOR);
                int onceLineCount = 3;
                for (int i = 0; i < lines.length; i += onceLineCount) {
                    String line = "";
                    for (int onceI = 0; onceI < onceLineCount; onceI ++) {

                        if (i + onceI < lines.length) {
                            if (!line.isEmpty()) {
                                line += LINE_SEPARATOR;
                            }
                            line += lines[i + onceI];
                        }
                    }

                    Log.d("fllog", line);
                }
                Log.d("fllog", "一一一一一一一一一一一一一一一一一一一一一一 结束 一一一一一一一一一一一一一一一一一一一一一一");
            }
        }.start();

    }
}
