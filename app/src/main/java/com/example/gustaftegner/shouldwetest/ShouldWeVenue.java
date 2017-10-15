package com.example.gustaftegner.shouldwetest;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by gustaftegner on 02/02/16.
 */
@ParseClassName("ShouldWeVenue")
public class ShouldWeVenue extends ParseObject {

    public ShouldWeVenue(){

    }
    public String getName(){
        return getString("name");
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint("location");
    }

    public ParseFile getImage(){
        return getParseFile("venueImage");
    }


    public String getAdress(){
        return getString("Address");
    }

    public String getZipcode(){
        return getString("Zipcode");
    }

    public JSONArray getVenueType(){
        //ArrayList<String> list = new ArrayList<>();
        return getJSONArray("venueType");
    }

    public JSONArray getOpeningHours(){
        return getJSONArray("openingHours");
    }

    public static ParseQuery<ShouldWeVenue> getQuery(){
        return ParseQuery.getQuery(ShouldWeVenue.class);
    }

}
