package com.example.gustaftegner.shouldwetest;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gustaftegner on 01/03/16.
 */
public class ChooseVenueDialog extends DialogFragment {

    public static final String TAG = ChooseVenueDialog.class.getSimpleName();
    private static final int MAX_POST_SEARCH_DISTANCE = 10;

    ParseUser mCurrentUser;
    private double lat;
    private double lon;
    Location currentLocation;

    private LinearLayout progressLayout;
    private Toolbar toolbar;
    private ListView list;

    onBarSelectedListener mCallBack;
    ArrayList<ShouldWeVenue> ShouldWeVenueList = new ArrayList<>();


    interface onBarSelectedListener {
        void onBarClicked(ShouldWeVenue venue);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mCallBack = (onBarSelectedListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement onBarSelectedListener");
        }
    }

    public static ChooseVenueDialog newInstance(int myIndex) {
        ChooseVenueDialog chooseVenueDialog = new ChooseVenueDialog();

        //example of passing args
        Bundle args = new Bundle();
        args.putInt("anIntToSend", myIndex);
        chooseVenueDialog.setArguments(args);

        return chooseVenueDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mCurrentUser = ParseUser.getCurrentUser();

        lat = getArguments().getDouble("Latitude");
        lon = getArguments().getDouble("Longitude");

        currentLocation = new Location("Location");
        currentLocation.setLongitude(lon);
        currentLocation.setLatitude(lat);

        Log.d(TAG, "position = " + lat + "," + lon);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //read the int from args
        int myInteger = getArguments().getInt("anIntToSend");

        View rootView = inflater.inflate(R.layout.fragment_choose_venue, container, false);

        list = (ListView) rootView.findViewById(R.id.list);
        progressLayout = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        loadVenues();

        /**RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();

         PlacesApi gplaces = restAdapter.create(PlacesApi.class);

         String sturep = "58.416333,15.618854";**/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkInPlaceAndGoBack(position);
            }
        });

        //here read the different parts of your layout i.e :
        //tv = (TextView) view.findViewById(R.id.yourTextView);
        //tv.setText("some text")

        return rootView;
    }

    public void loadVenues(){

        getParsePlacesNear(currentLocation);
    }

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public void getParsePlacesNear(Location location){


        final VenueAdapter adapter = new VenueAdapter(getActivity(), ShouldWeVenueList);
        list.setAdapter(adapter);

        Log.d(TAG, "GetParsePLacesNear " + Utils.locationToString(location));
        final ParseGeoPoint myPoint = geoPointFromLocation(location);
        ParseQuery<ShouldWeVenue> venueQuery =ShouldWeVenue.getQuery();

        venueQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);


        venueQuery.findInBackground(new FindCallback<ShouldWeVenue>() {
            @Override
            public void done(List<ShouldWeVenue> list, ParseException e) {
                if (e == null) {

                    ShouldWeVenueList.clear();

                    //only gets places if it has an image
                    for (int i = 0; i < list.size(); i++) {
                        ShouldWeVenue venue = list.get(i);
                        if (venue.getImage() != null) {
                            ShouldWeVenueList.add(venue);
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "added " + venue.getName() + " to list");
                        }
                    }
                    //ShouldWeVenueList.addAll(list);


                    Log.d(TAG, "we got parse places");
                    Log.d(TAG, String.valueOf(ShouldWeVenueList.size()));
                    //updateMarkers();




                } else {
                    Log.d(TAG, "Couldnt get parsevenues: " + e.toString());
                }
            }
        });
    }

    public void checkInPlaceAndGoBack(int position){
        //com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result result = venueDetailsResult.get(position);
        //String id = result.getPlaceId();
        //String name = result.getName();

        ShouldWeVenue venue = ShouldWeVenueList.get(position);
        Log.d(TAG, "checkinplace");
        mCallBack.onBarClicked(venue);
    }

}
