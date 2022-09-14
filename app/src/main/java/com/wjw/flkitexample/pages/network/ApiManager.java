package com.wjw.flkitexample.pages.network;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wjw.flkit.unit.FLAsyncTask;
import com.wjw.flkit.unit.FLLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(manager().getOkHttpClient())
                .baseUrl(placeholderURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }
    protected HashMap<String, ApiLoader> getDataLoaders() {
        return dataLoaders;
    }
    public static ApiManager manager() {
        return manager;
    }
    private static String placeholderURL = "https://placholder";
    private static ApiManager manager = new ApiManager();
    private String domain = "http://api.map.baidu.com";
    private HashMap<String, ApiLoader> dataLoaders = new HashMap<>();
    private OkHttpClient okHttpClient = null;
    private ApiManager() {}
    private OkHttpClient getOkHttpClient() {
        //不为空则说明已经配置过了，直接返回即可。
        if (okHttpClient == null) {
            //OkHttp构建器
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置网络请求超时时长，这里设置为6s
            builder.connectTimeout(15, TimeUnit.SECONDS);
            //添加请求拦截器，如果接口有请求头的话，可以放在这个拦截器里面
            builder.addInterceptor(new RequestInterceptor());
            //添加返回拦截器，可用于查看接口的请求耗时，对于网络优化有帮助
            builder.addNetworkInterceptor(new ResponseInterceptor());
            //OkHttp配置完成
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }
    private void requestDomain() {
        try {
            Request request = new Request.Builder()
                    .url("动态请求域名")
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String string = response.body().string();
                JSONObject data = JSON.parseObject(string, JSONObject.class);
                if (data.getInteger("code") == 1) {
                    data = data.getJSONObject("data");
                    domain = data.getString("url");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class RequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            while (domain == null || domain.isEmpty()) {
                requestDomain();
            }
            String path = chain.request().url().url().toString();
            path = path.replace(placeholderURL, "");
            String url = domain;
            if (path.startsWith("/")) {
                if (url.endsWith("/")) {
                    path = path.substring(1);
                }
            }
            else {
                if (!url.endsWith("/")) {
                    path = "/" + path;
                }
            }
            Request.Builder builder = chain.request().newBuilder();
            builder.url(url + path);
            builder.addHeader("system", "ios");
            return chain.proceed(builder.build());
        }
    }
    private class ResponseInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            HashMap<String, String> headers = new HashMap<>();
            for (int i = 0; i < chain.request().headers().size(); i++) {
                headers.put(chain.request().headers().name(i), chain.request().headers().value(i));
            }
            Response response = chain.proceed(chain.request());
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            String string = buffer.clone().readString(Charset.forName("UTF-8"));
            HashMap result = JSON.parseObject(string, HashMap.class);
            if (result == null) {
                result = new HashMap();
            }
            HashMap map = new HashMap();
            map.put("1.method", response.request().method());
            map.put("2.url", response.request().url().url().toString());
            map.put("3.headers", headers);
            map.put("4.response", result);
            if (result != null) {
                int status = (int) result.get("status");
                if (status != 200) {
                    FLLog.logMap(map);
                    throw new IOException((String) result.get("message"));
                }
            }
            else {
                result = new HashMap();
            }
            FLLog.logMap(map);
            return response;
        }
    }
}
