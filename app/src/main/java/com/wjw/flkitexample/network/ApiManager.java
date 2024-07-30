package com.wjw.flkitexample.network;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkit.BuildConfig;
import com.wjw.flkit.unit.FLLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(manager().getOkHttpClient())
                .baseUrl(domain)
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
    private static ApiManager manager = new ApiManager();
    private static String domain = "http://api.map.baidu.com";
    private HashMap<String, ApiLoader> dataLoaders = new HashMap<>();
    private OkHttpClient okHttpClient = null;
    private ApiManager() {}
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(15, TimeUnit.SECONDS);
            builder.addNetworkInterceptor(new NetworkInterceptor());
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private class NetworkInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            {
                Request.Builder builder = request.newBuilder();
                builder.addHeader("system", "android");
                request = builder.build();
            }
            Response response = chain.proceed(request);
            if (BuildConfig.DEBUG) {
                FLLog.logMap(new HashMap<String, Object>() {{
                    put("1.method", response.request().method());
                    put("2.url", response.request().url().toString());
                    put("3.headers", getRequestHeaders(response.request()));
                    put("4.params", getRequestParams(response.request()));
                    put("5.response", getResponseData(response));
                }});
            }
            return response;
        }
    }
    private HashMap<String, Object> getRequestHeaders(Request request) {
        HashMap<String, Object> headers = new HashMap<>();
        for (int i = 0; i < request.headers().size(); i++) {
            headers.put(request.headers().name(i), request.headers().value(i));
        }
        return headers;
    }
    private HashMap<String, Object> getRequestParams(Request request) {
        Buffer bodyBuffer = new Buffer();
        HashMap params = new HashMap();
        try {
            request.body().writeTo(bodyBuffer);
            String requestBodyToString = bodyBuffer.readUtf8();
            if (!(request.body() instanceof MultipartBody)) {
                try {
                    params = JSONObject.parseObject(requestBodyToString, HashMap.class);
                } catch (Exception exception) {
                    if (requestBodyToString.contains("&")) {
                        String[] values = requestBodyToString.split("&");
                        for (String value : values) {
                            if (value.contains("=")) {
                                String[] keyValue = value.split("=");
                                if (keyValue.length == 2) {
                                    params.put(keyValue[0], keyValue[1]);
                                } else {
                                    params.put(keyValue[0], "");
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {

        }
        return params;
    }
    private HashMap getResponseData(Response response) {

        try {
            BufferedSource source = response.body().source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            String resultString = buffer.clone().readString(Charset.forName("UTF-8"));
            return JSONObject.parseObject(resultString, HashMap.class);
        } catch (Exception e) {

        }
        return new HashMap<String, Object>();
    }
}
