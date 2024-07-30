package com.wjw.flkitexample.network.api;

import com.alibaba.fastjson.JSONObject;
import com.wjw.flkitexample.network.respon.ApiParams;
import com.wjw.flkitexample.network.respon.ObjectRespon;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface TestApi {
    /**
     * 接口测试
     * @return
     */
    @GET("/telematics/v3/weather?location=嘉兴&output=json&ak=5slgyqGDENN7Sy7pw29IUvrZ")
    Observable<ObjectRespon<JSONObject>> getTest();

    /**
     * 首页数据
     */
    @FormUrlEncoded
    @POST("/mb/index")
    Observable<ObjectRespon<List<JSONObject>>> getHomeData(@FieldMap ApiParams params);
}
