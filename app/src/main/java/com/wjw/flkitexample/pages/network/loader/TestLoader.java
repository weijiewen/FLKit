package com.wjw.flkitexample.pages.network.loader;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkitexample.pages.network.ApiLoader;
import com.wjw.flkitexample.pages.network.ApiManager;
import com.wjw.flkitexample.pages.network.api.TestApi;
import com.wjw.flkitexample.pages.network.respon.BaseObjectRespon;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class TestLoader extends ApiLoader {
    private TestApi testApi;
    private static TestLoader loader = new TestLoader();
    private TestLoader() {
        testApi = ApiManager.getRetrofit().create(TestApi.class);
    }
    public static Observable<BaseObjectRespon<JSONObject>> getTest() {
        return loader.observe(loader.testApi.getTest());
    }
}