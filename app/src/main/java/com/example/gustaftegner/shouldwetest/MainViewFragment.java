package com.example.gustaftegner.shouldwetest;

import android.app.Dialog;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.gustaftegner.shouldwetest.API.PlacesApi;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.GoogleJSON;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Result;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by gustaftegner on 01/02/16.
 */
public class MainViewFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public final static String TAG = MainViewFragment.class.getSimpleName();

    private StaggeredGridLayoutManager gaggeredGridLayoutManager;

    //Location stuff
    private Location currentLocation;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;

    List<Result> venueList = new LinkedList<>();

    String API = GoogleConstants.BASE_URL;
    String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/details";

    /**
     * Location STATICS
     *
     */

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    /*
     * Constants for location update parameters
     */
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // The update interval
    private static final int UPDATE_INTERVAL_IN_SECONDS = 5;

    // A fast interval ceiling
    private static final int FAST_CEILING_IN_SECONDS = 1;

    // Update interval in milliseconds
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;

    // A fast ceiling of update intervals, used when the app is visible
    private static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = MILLISECONDS_PER_SECOND
            * FAST_CEILING_IN_SECONDS;



    //new stuff
    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;

    private static final int MAX_POST_SEARCH_DISTANCE = 10;

    ArrayList<ShouldWeVenue> ShouldWeVenueList = new ArrayList<>();

    SolventRecyclerViewAdapter rcAdapter;


    RecyclerView recyclerView;


    private ParseQueryAdapter<ShouldWeVenue> mainAdapter;

    public MainViewFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
        // Create a new global location parameters object

        rcAdapter = new SolventRecyclerViewAdapter(getActivity(), ShouldWeVenueList);

        buildGoogleApiClient();


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        Log.d(TAG, "oncreate view called");
        View v = inflater.inflate(R.layout.fragment_mainview, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(1,1);
        //altGridLayoutManager = new CustomView(2,1);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);

        recyclerView.setAdapter(rcAdapter);
        return v;
    }



    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause()  {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConnected(Bundle bundle) {

        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "your location is: " + Utils.locationToString(mLastLocation));
            getParsePlacesNear(mLastLocation);
        } else {
            Toast.makeText(getActivity(), "No location detected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }






    public void getParsePlacesNear(Location location){

        Log.d(TAG, "GetParsePLacesNear " + Utils.locationToString(location));
        final ParseGeoPoint myPoint = geoPointFromLocation(location);
        ParseQuery<ShouldWeVenue> venueQuery =ShouldWeVenue.getQuery();

        venueQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);


        venueQuery.findInBackground(new FindCallback<ShouldWeVenue>() {
            @Override
            public void done(List<ShouldWeVenue> list, ParseException e) {
                if(e == null){

                    ShouldWeVenueList.clear();

                    //only gets places if it has an image
                    for (int i = 0; i<list.size(); i++){
                        ShouldWeVenue venue = list.get(i);
                        if(venue.getImage() != null){
                            ShouldWeVenueList.add(venue);
                            Log.d(TAG, "added " + venue.getName());
                        }
                    }
                    //ShouldWeVenueList.addAll(list);


                    Log.d(TAG, "we got parse places");
                    Log.d(TAG, String.valueOf(ShouldWeVenueList.size()));

                    //ShouldWeVenueList = (ArrayList<ShouldWeVenue>) list;

                    //Log.d(TAG, "Should we venue list size = " + String.valueOf(ShouldWeVenueList.size()));
                    rcAdapter.notifyDataSetChanged();
                    /**for (ShouldWeVenue venue : list){
                        ShouldWeVenueList.add(venue);
                        Log.d(TAG, "data loaded into list");
                        //rcAdapter.notifyDataSetChanged();
                     }**/

                } else {
                    Log.d(TAG, "Couldnt get parsevenues: " + e.toString());
                }
            }
        });
    }




    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        /**
         * Default constructor. Sets the dialog field to null
         */
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        /*
         * Set the dialog to display
         *
         * @param dialog An error dialog
         */
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        /*
         * This method must return a Dialog to the DialogFragment.
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}


