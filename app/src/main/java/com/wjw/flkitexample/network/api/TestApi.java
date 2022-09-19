package com.wjw.flkitexample.network.api;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkitexample.network.respon.BaseObjectRespon;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface TestApi {
    /**
     * 接口测试
     * @return
     */
    @GET("/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ")
    Observable<BaseObjectRespon<JSONObject>> getTest();
}
