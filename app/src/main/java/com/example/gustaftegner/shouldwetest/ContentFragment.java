package com.example.gustaftegner.shouldwetest;

/**
 * Created by gustaftegner on 16/10/15.
 */
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gustaftegner.shouldwetest.API.PlaceDetailsApi;
import com.example.gustaftegner.shouldwetest.API.PlacesApi;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Geometry;
import com.example.gustaftegner.shouldwetest.GoogleJSONstuff.GoogleJSON;
import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Photo;
import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.PlaceDetails;
import com.example.gustaftegner.shouldwetest.GooglePlacesDetailsStuff.Result;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Admin on 04-06-2015.
 */
public class ContentFragment extends Fragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = ContentFragment.class.getSimpleName();


    /*
    * Define a request code to send to Google Play services This code is returned in
    * Activity.onActivityResult
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


    /*
   * Constants for handling location results
   */
    // Conversion from feet to meters
    private static final float METERS_PER_FEET = 0.3048f;

    // Conversion from kilometers to meters
    private static final int METERS_PER_KILOMETER = 1000;

    // Initial offset for calculating the map bounds
    private static final double OFFSET_CALCULATION_INIT_DIFF = 1.0;

    // Accuracy for calculating the map bounds
    private static final float OFFSET_CALCULATION_ACCURACY = 0.01f;

    // Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;

    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;

    // Map fragment
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;



    // Represents the circle around a map
    private Circle mapCircle;

    // Fields for the map radius in feet
    private float radius;
    private float lastRadius;

    // Fields for helping process map and location changes
    private int mostRecentMapUpdate;
    private boolean hasSetUpInitialLocation;
    private String selectedPostObjectId;
    private Location lastLocation;
    private Location currentLocation;

    // A request to connect to Location Services
    private LocationRequest locationRequest;

    // Stores the current instantiation of the location client in this object
    private GoogleApiClient locationClient;


    // Floating Action Button
    FloatingActionButton floatingActionButton;


    //GOOGLE PLACES STUFF
    List<com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Result> venueList = new LinkedList<>();

    String API = GoogleConstants.BASE_URL;
    String PLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/details";

    private final Map<Marker, String> mapMarkers = new HashMap<Marker, String>();
    private Map<String, Marker> stringMarkerMap = new HashMap<>();

    //private ArrayList<String> reviewList = new ArrayList<>();

    //Bottomsheet
    BottomSheetLayout mBottomSheet;


    private Marker selectedMarker;

    onFabListener mCallback;

    public interface onFabListener{
        public void onFabClicked(Location location);
    }



    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            mCallback = (onFabListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement onFabListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        radius = 250.0f; //Hardcoded, shoudl be in settings
        lastRadius = radius;
        //setContentView(R.layout.activity_main);

        // Create a new global location parameters object
        locationRequest = LocationRequest.create();

        // Set the update interval
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest.setFastestInterval(FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Create a new location client, using the enclosing class to handle callbacks.
        locationClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        locationClient.connect();

        /*MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/




    }
    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_fragment, container, false);

        //Button b = (Button) v.findViewById(R.id.post_button);

        mBottomSheet = (BottomSheetLayout) v.findViewById(R.id.bottomsheet);
        mBottomSheet.setShouldDimContentView(false);

        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();

        mMap.setMyLocationEnabled(true);



        // Set up the camera change handler
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            public void onCameraChange(CameraPosition position) {
                // When the camera changes, update the query
                doMapQuery(position);
            }
        });


        mMap.getUiSettings().setMapToolbarEnabled(false);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (selectedMarker != marker) {
                    if (selectedMarker != null) {
                        selectedMarker.setIcon(BitmapDescriptorFactory.defaultMarker());
                    }
                }
                String placeId = mapMarkers.get(marker);
                Log.d(TAG, "onMarkerClick called");
                Log.d(TAG, placeId);

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                marker.setIcon(bitmapDescriptor);
                selectedMarker = marker;

                getPlaceDetails(placeId);
                //TextView tv = (TextView) getActivity().findViewById(R.id.venueLabelText);
                //tv.setText(placeId);


                return false;
            }
        });

        //Initialize fab
        floatingActionButton = (FloatingActionButton) v.findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "fab clicked", Toast.LENGTH_SHORT).show();


                Log.d(TAG, String.valueOf(currentLocation.getLatitude()));
                mCallback.onFabClicked(currentLocation);

                //ChooseVenueFragment chooseVenueFragment = new ChooseVenueFragment();


                /**actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                 getSupportFragmentManager().beginTransaction().replace(R.id.frame, chooseVenueFragment, "chooseVenue").addToBackStack(null).commit();

                 getSupportActionBar().setTitle("Check in");**/

                //replaceFragment(chooseVenueFragment, true, getActivity());


                //getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                //Show checkin fragment
                //CheckInDialog
            }
        });


        return v;
    }



    public void doMapQuery(CameraPosition position){
        /**if(currentLocation != null) {
            getPlacesNear(currentLocation);
        }**/

        LatLng pos = position.target;

        Location l = new Location("Test");
        l.setLatitude(pos.latitude);
        l.setLongitude(pos.longitude);
        getPlacesNear(l);

        //Update map and markers

    }



    private void cleanUpMarkers(){
        mapMarkers.clear();
        mMap.clear();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        Location location = getLocation();
        //Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);
        if(location == null){
            LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        }else{
            currentLocation  =getLocation();
            //currentGeoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());


            /**mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 13));**/

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(14)                   // Sets the zoom
                            // Sets the orientation of the camera to east
                            // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
          mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //handleNewLocation(location);

            Log.d(TAG, "Trying to get tips nearby");

            getPlacesNear(location);
        }

        Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_SHORT).show();
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            if (Application.APPDEBUG) {
                // In debug mode, log the status
                Log.d(Application.APPTAG, "Google play services available");
            }
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
            if (dialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(dialog);
                errorFragment.show(getFragmentManager(), Application.APPTAG);
            }
            return false;
        }
    }

    @Override
    public void onStop(){
        locationClient.disconnect();
        super.onStop();
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(Application.APPTAG, "GoogleApiClient connection has been suspend");

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed: " + location.toString());
        currentLocation = location;
    }

    private Location getLocation() {
        // If Google Play Services is available
        if (servicesConnected()) {
            // Get the current location
            return LocationServices.FusedLocationApi.getLastLocation(locationClient);
        } else {
            return null;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Play services can resolve some errors it detects. If the error has a resolution, try
        // sending an Intent to start a Google Play services activity that can resolve error.
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (IntentSender.SendIntentException e) {

                if (Application.APPDEBUG) {
                    // Thrown if Google Play services canceled the original PendingIntent
                    Log.d(Application.APPTAG, "An error occurred when connecting to location services.", e);
                }
            }
        } else {
            // If no resolution is available, display a dialog to the user with the error.
            showErrorDialog(connectionResult.getErrorCode());
        }
    }



    private void showErrorDialog(int errorCode) {
        // Get the error dialog from Google Play services
        Dialog errorDialog =
                GooglePlayServicesUtil.getErrorDialog(errorCode, getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);

        // If Google Play services can provide an error dialog
        if (errorDialog != null) {

            // Create a new DialogFragment in which to show the error dialog
            ErrorDialogFragment errorFragment = new ErrorDialogFragment();

            // Set the dialog in the DialogFragment
            errorFragment.setDialog(errorDialog);

            // Show the error dialog in the DialogFragment
            errorFragment.show(getFragmentManager(), Application.APPTAG);
        }
    }


    public static String locationToString(Location loc){
        String lat = String.valueOf(loc.getLatitude());
        String lon = String.valueOf(loc.getLongitude());

        return lat+","+lon;
    }

    public void getPlaceDetails(final String placeId){
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(PLACES_BASE_URL).build();

        PlaceDetailsApi detailsApi = restAdapter.create(PlaceDetailsApi.class);

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.place_info, mBottomSheet, false);
        final ProgressBar spinner = (ProgressBar) v.findViewById(R.id.progressBar);

        final ArrayList<UserReview> UserReviewList = new ArrayList<>();

        spinner.setVisibility(View.VISIBLE);
        mBottomSheet.showWithSheetView(v);

        RatingBar userRating = (RatingBar)v.findViewById(R.id.rating_text);
        SeekBar seekBar = (SeekBar)v.findViewById(R.id.venue_seekbar);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        userRating.setRating(0);
        seekBar.setProgress(0);

        //1 request

        ParseQuery<UserReview> reviews = ParseQuery.getQuery(UserReview.class);
        reviews.whereEqualTo("VenueID", placeId);
        reviews.findInBackground(new FindCallback<UserReview>() {
            @Override
            public void done(List<UserReview> list, ParseException e) {
                if(e == null){
                    Log.d(TAG, "Successfully got review");

                    for(UserReview obj : list){
                        //String review = obj.getReviewText();

                        UserReviewList.add(obj);
                        //Log.d(TAG, "reviewText = " + review);
                        Log.d("FromReviewer", "From " + obj.getFromFBUserId());
                        //reviewList.add(review);
                        //updateBottomSheetList(v, reviewList);
                        updateBottomSheetList(v, UserReviewList);
                    }
                }else{
                    Log.d(TAG, "Error retrieving ParseObjects");
                }
            }
        });

        //End
        detailsApi.getVenueDetails(placeId,GoogleConstants.API_KEY, new Callback<PlaceDetails>() {

            @Override
            public void success(PlaceDetails placeDetails, Response response) {
                spinner.setVisibility(View.GONE);
                Log.d(TAG, "Success details: ");
                Result resultDetails = placeDetails.getResult();


                updateBottomSheetView(v, placeDetails);
                //mBottomSheet.showWithSheetView(v2);
                //showBottomSheet(placeDetails);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Failed to get details: " + error.toString());

            }
        });



    }

    public void updateBottomSheetList(View v, ArrayList<UserReview> reviewList){
        ListView list = (ListView) v.findViewById(R.id.listView);

        list.setItemsCanFocus(true);
        list.setClickable(false);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, reviewList);

        CommentAdapter adapter1 = new CommentAdapter(getActivity(), reviewList);
        list.setAdapter(adapter1);
        double intensity = 0;
        double rating = 0;
        int count = reviewList.size();
        for(UserReview r : reviewList){
            rating = rating + r.getReviewRating();
            intensity = intensity + r.getCrowdVolume();
        }

        int avgRating = 0;
        int avgCrowd = 0;

        if(count != 0){


        avgRating = (int) (rating / count);
        Log.d(TAG, "rating: " + String.valueOf(avgRating));
        avgCrowd = (int) (intensity / count);

        }


        RatingBar userRating = (RatingBar)v.findViewById(R.id.rating_text);
        SeekBar seekBar = (SeekBar)v.findViewById(R.id.venue_seekbar);

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Log.d(TAG, "avgrating, avgcrownd: " + avgRating + " " + avgCrowd);

        userRating.setRating(avgRating);

        seekBar.setProgress(avgCrowd);




    }


    public void updateBottomSheetView(View v, PlaceDetails placeDetails){
        Log.d(TAG, "Show bottomsheet called");
        Result result = placeDetails.getResult();

        CircleImageView barImage = (CircleImageView) v.findViewById(R.id.bar_image);

        List<Photo> photoArray = result.getPhotos();
        if(photoArray.size() > 0){
            Log.d(TAG, result.getName() + " has a photo");
            Photo photo = photoArray.get(0);
            String photoReference = photo.getPhotoReference();
            String photoURL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1000&photoreference=" + photoReference + "&key=" + GoogleConstants.API_KEY;
            //Picasso.with(getActivity()).load(photoURL).into(barImage);
            Picasso.with(getActivity())
                    .load(photoURL)
                    .fit()
                    .centerCrop()
                    .into(barImage);


        }
         //Get first photo




        //View v = LayoutInflater.from(getActivity()).inflate(R.layout.place_info, mBottomSheet, false);
        TextView venueName = (TextView) v.findViewById(R.id.venueLabelText);


        /**TextView phoneNumber = (TextView) v.findViewById(R.id.phoneLabel);
        TextView address = (TextView) v.findViewById(R.id.addressLabel);
        TextView openingTimes = (TextView) v.findViewById(R.id.openingTimesLabel);**/

        venueName.setText(result.getName());

        /**phoneNumber.setText(result.getFormattedPhoneNumber());
        address.setText(result.getFormattedAddress());
        //openingTimes.setText("Is open now: " + result.getOpeningHours().getOpenNow().toString());
        openingTimes.setText("Rating: " + String.valueOf(result.getRating()));**/





        //return v;
        //mBottomSheet.showWithSheetView(v);

    }
    public void getPlacesNear(Location location){
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL).setEndpoint(API).build();

        PlacesApi gplaces = restAdapter.create(PlacesApi.class);

        String currentLocationString = locationToString(location);
        String radius = "500";
        VisibleRegion visibleRegion = mMap.getProjection().getVisibleRegion();


        gplaces.getVenues(radius, currentLocationString, new Callback<GoogleJSON>() {
            @Override
            public void success(GoogleJSON googleJSON, Response response) {
                Log.d(TAG, "We got places");


                venueList = googleJSON.getResults();
                mapListToMap(venueList);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failed somehow " + error.toString());
            }
        });
    }

    public void mapListToMap(List<com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Result> list){
        Log.d(TAG, "mapListToMap called");
        for(com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Result result : list){
            if(stringMarkerMap.get(result.getPlaceId()) == null){
                //Add a marker
                Geometry g = result.getGeometry();

                com.example.gustaftegner.shouldwetest.GoogleJSONstuff.Location location = g.getLocation();
                LatLng latlng = new LatLng(location.getLat(), location.getLng());

                MarkerOptions markerOptions = new MarkerOptions().position(latlng);

                Marker marker = mMap.addMarker(markerOptions);

                mapMarkers.put(marker,result.getPlaceId());
                stringMarkerMap.put(result.getPlaceId(), marker);
            }


            //mMap.addMarker(new MarkerOptions().position(latlng).title("VENUE"));

        }
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

