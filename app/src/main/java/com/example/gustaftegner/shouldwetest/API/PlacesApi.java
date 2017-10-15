package com.example.gustaftegner.shouldwetest.API;

import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.GoogleJSON;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by gustaftegner on 23/10/15.
 */
public interface PlacesApi {


    @GET("/json?radius=500&types=night_club|bar&key=AIzaSyBkUrdqFmX1_Ycy4FeQyxBo4ts8uCem3-o")
    void getVenues(@Query(value = "radius") String radius,
            //@Query(value = "key") String api_key,
            @Query(value = "location") String location, Callback<GoogleJSON> response);



    //public void getVenues(@Path("location") String latLng, @Path("API_KEY") String API_KEY, Callback<GoogleJSON> response);


}
