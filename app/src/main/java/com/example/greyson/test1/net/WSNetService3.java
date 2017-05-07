package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.TrackerRes;

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


public interface WSNetService3 {

    @GET("json?")
    Observable<TrackerRes> getDuration(@QueryMap Map<String, String> params);
}