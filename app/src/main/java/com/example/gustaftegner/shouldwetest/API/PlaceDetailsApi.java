package com.example.gustaftegner.shouldwetest.API;

import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.PlaceDetails;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by gustaftegner on 25/10/15.
 */
public interface PlaceDetailsApi {

    @GET("/json")
    void getVenueDetails(
            //@Query(value = "key") String api_key,
            @Query(value = "placeid") String placeId,@Query(value = "key") String api, Callback<PlaceDetails> callback);

}
