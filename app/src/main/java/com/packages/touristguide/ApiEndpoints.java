package com.packages.touristguide;

import com.packages.touristguide.Map.Geocode;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Jannat Mostafiz on 4/6/2018.
 */

public interface ApiEndpoints {
    //String url="http://maps.googleapis.com/maps/api/geocode/json?address=khulna";
    @GET("/maps/api/geocode/json")
    Call<Geocode> getGeocodeByAddress(@QueryMap Map<String, String> params);
}