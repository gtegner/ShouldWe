package com.example.gustaftegner.shouldwetest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.gustaftegner.shouldwetest.API.PlaceDetailsApi;
import com.example.gustaftegner.shouldwetest.API.PlacesApi;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.GoogleJSON;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Result;
import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.PlaceDetails;
import com.google.android.gms.maps.model.VisibleRegion;
import com.parse.ParseUser;

import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gustaftegner on 25/10/15.
 */
public class ChooseVenueFragment extends Fragment {


    ParseUser mCurrentUser;
    public final static String TAG = ChooseVenueFragment.class.getSimpleName();

    List<Result> venueList;
    List<com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result> venueDetailsResult = new LinkedList<>();

    private double lat;
    private double lon;
    String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/details";


    private LinearLayout progressLayout;
    private Toolbar toolbar;
    private ListView list;

    onBarSelectedListener mCallBack;

    interface onBarSelectedListener {
        void onBarClicked(String placeId, String name);
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

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called");
        //setHasOptionsMenu(true);


        mCurrentUser = ParseUser.getCurrentUser();

        lat = getArguments().getDouble("Latitude");
        lon = getArguments().getDouble("Longitude");

        Log.d(TAG, "position = " + lat + "," + lon);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_choose_venue, container, false);

        list = (ListView) rootView.findViewById(R.id.list);
        progressLayout = (LinearLayout) rootView.findViewById(R.id.linlaHeaderProgress);

        //loadVenues();

        /**RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();

         PlacesApi gplaces = restAdapter.create(PlacesApi.class);

         String sturep = "58.416333,15.618854";**/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkInPlaceAndGoBack(position);
            }
        });

        return rootView;
    }

    private void loadVenues() {
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(GoogleConstants.BASE_URL).build();

        PlacesApi gplaces = restAdapter.create(PlacesApi.class);

        String currentLocationString = lat + "," + lon;
        String radius = "1000";

        //final VenueAdapter adapter = new VenueAdapter(getActivity(), venueDetailsResult);
        //list.setAdapter(adapter);


        //Find venues

        gplaces.getVenues(radius, currentLocationString, new Callback<GoogleJSON>() {
            @Override
            public void success(GoogleJSON googleJSON, Response response) {
                Log.d(TAG, "We got places");
                Log.d(TAG, "Number of places: " + googleJSON.getResults().size());
                RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(PLACES_BASE_URL).build();

                PlaceDetailsApi detailsApi = restAdapter.create(PlaceDetailsApi.class);

                venueList = googleJSON.getResults();
                for (Result result : venueList) {
                    detailsApi.getVenueDetails(result.getPlaceId(), GoogleConstants.API_KEY, new Callback<PlaceDetails>() {
                        @Override
                        public void success(PlaceDetails placeDetails, Response response) {
                            com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result detailResult = placeDetails.getResult();
                            Log.d(TAG, detailResult.getName());
                            venueDetailsResult.add(detailResult);
                            Log.d(TAG, String.valueOf(venueDetailsResult.size()));
                            //adapter.notifyDataSetChanged();


                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(TAG, "Failed to get place with error: " + error.toString());
                        }
                    });
                }

                Log.d(TAG, "setting list adapter");

                //ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, );

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failed somehow " + error.toString());
            }
        });
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    public void checkInPlaceAndGoBack(int position){
        com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result result = venueDetailsResult.get(position);
        String id = result.getPlaceId();
        String name = result.getName();
        Log.d(TAG, "PlaceID = " + id);
        mCallBack.onBarClicked(id, name);
    }


    /**@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get item selected and deal with it
        int id = item.getItemId();
        Log.d(TAG, "Item id: " + id);
        if(id == R.id.home){
            Log.d(TAG, "home pressed");
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }**/


    @Override
    public void onResume(){


        super.onResume();

        //get new position
        lat = getArguments().getDouble("Latitude");
        lon = getArguments().getDouble("Longitude");

        loadVenues();



    }
}
