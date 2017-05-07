package com.example.greyson.test1.net;

import com.example.greyson.test1.entity.DeletePinRes;
import com.example.greyson.test1.entity.GetAllPinRes;
import com.example.greyson.test1.entity.GetMyPinRes;
import com.example.greyson.test1.entity.RouteRes;
import com.example.greyson.test1.entity.SafePlaceRes;
import com.example.greyson.test1.entity.SavePinRes;
import com.example.greyson.test1.entity.TrackerRes;

import java.util.Map;

import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;


/**
 * This class is interface
 *
 * @author Greyson, Carson
 * @version 1.0
 */

public interface WSNetService {
    /**
     * SafePlace Request
     */
    @GET("?format=json&")
    Observable<SafePlaceRes> getSafePlaceData(@QueryMap Map<String, String> params);

    @GET
    Observable<SavePinRes> getSavePinData(@Url String url,@QueryMap Map<String, String> params);

    @GET
    Observable<GetAllPinRes> getAllPinData(@Url String url, @QueryMap Map<String, String> params);

    @GET
    Observable<GetMyPinRes> getMyPinData(@Url String url, @QueryMap Map<String, String> params);

    @GET
    Observable<DeletePinRes> getDeletePinData(@Url String url, @QueryMap Map<String, String> params);

    @GET("json?")
    Observable<RouteRes> getSafePlaceRoute(@Url String url, @QueryMap Map<String, String> params);

    @GET("json?")
    Observable<TrackerRes> getDuration(@Url String url, @QueryMap Map<String, String> params);

}
