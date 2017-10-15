package com.example.gustaftegner.shouldwetest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import retrofacebook.Facebook;
import retrofacebook.Post;

/**
 * Created by gustaftegner on 09/11/15.
 */
public class FriendsFragment extends Fragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();
    ListView mFriendList;
    ParseUser mCurrentUser;

    private HashMap<String, String> fbUserMap = new HashMap<>();

    public FriendsFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mCurrentUser = ParseUser.getCurrentUser();

        Log.d(TAG, "onCreate called");




        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject obj = response.getJSONObject();
                        try{
                            JSONArray result = obj.getJSONArray("data");

                            for (int i = 0; i<result.length(); i++){
                                JSONObject user = result.getJSONObject(i);
                                fbUserMap.put(user.getString("id"),user.getString("name"));
                                Log.d(TAG, fbUserMap.get(user.getString("id")));
                            }
                            Log.d(TAG, "result is: " + result);
                        }catch (JSONException e){
                            Log.d(TAG, "error with fb: " + e.toString());
                        }
                        Log.d(TAG, obj.toString());
                    }
                }
        ).executeAsync();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = (ListView) rootView.findViewById(R.id.friend_list);



        return rootView;

    }



}
