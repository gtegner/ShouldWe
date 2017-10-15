package com.example.gustaftegner.shouldwetest;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by gustaftegner on 09/11/15.
 */

@ParseClassName("UserReview")
public class UserReview extends ParseObject {

    public UserReview(){

    }

    public String getVenueName(){
        return getString("VenueName");
    }

    public String getReviewText(){
        return getString("ReviewText");

    }

    public ParseObject getAtVenue(){
        return getParseObject("atVenue");
    }

    public int getReviewRating(){
        return getInt("ReviewRating");
    }

    public ParseUser getFromUser(){ return getParseUser("fromParseUser");}

    public String getVenueId(){
        return getString("VenueID");
    }

    public String getFromUserId(){
        return getString("FromUserId");
    }

    public String getFromFBUserId(){
        return getString("FromFBUserId");
    }

    public int getLikeCount(){
        return getInt("LikeCount");
    }

    public int getCrowdVolume(){
        return getInt("CrowdVolume");
    }

    public String getFromFBUserName(){
        return getString("FromFBUserName");
    }

    public void setFromFBUserName(String text){
        put("FromFBUserName", text);
    }

    public void setReviewText(String text){
        put("ReviewText", text);
    }

    public void setVenueId(String text){
        put("VenueID", text);
    }

    public void setFromUserId(String text){
        put("FromUserId", text);
    }

    public void setFromFBUserId(String text){
        put("FromFBUserId", text);
    }

    public void setLikeCount(int count){
        put("LikeCount", count);
    }

    public void setVenueName(String text){
        put("VenueName", text);
    }

    public void setReviewRating(int rating){
        put("ReviewRating", rating);
    }

    public void setCrowdVolume(int people){
        put("CrowdVolume", people);
    }

    public void setFromUser(ParseUser user){ put("fromParseUser", user);}
}


