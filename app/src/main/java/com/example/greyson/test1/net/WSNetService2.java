package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.RouteRes;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * This class is interface
 *
 * @author Greyson, Carson
 * @version 1.0
 */


public interface WSNetService2 {

    @GET("json?")
    Observable<RouteRes> getSafePlaceRoute(@QueryMap Map<String, String> params);
}