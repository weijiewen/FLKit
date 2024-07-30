package com.wjw.flkitexample.network.loader;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkitexample.network.ApiLoader;
import com.wjw.flkitexample.network.ApiManager;
import com.wjw.flkitexample.network.api.TestApi;
import com.wjw.flkitexample.network.respon.ObjectRespon;

import io.reactivex.Observable;

public class TestLoader extends ApiLoader {
    private TestApi testApi;
    private static TestLoader loader = new TestLoader();
    private TestLoader() {
        testApi = ApiManager.getRetrofit().create(TestApi.class);
    }
    public static Observable<ObjectRespon<JSONObject>> getTest() {
        return loader.observe(loader.testApi.getTest());
    }
}