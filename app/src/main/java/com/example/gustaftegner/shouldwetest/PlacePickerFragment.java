package com.example.gustaftegner.shouldwetest;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.model.CameraPosition;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.MapboxConstants;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.views.MapView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlacePickerFragment extends Fragment implements LocationListener {

    public static final String TAG = PlacePickerFragment.class.getSimpleName();
    private static final int MAX_POST_SEARCH_DISTANCE = 10;

    String API = GoogleConstants.BASE_URL;
    ParseUser currentUser;

    Location lastLocation = null;
    LocationServices locationServices;
    private MapView mapView = null;

    private Map<String, ShouldWeVenue> mapMarkers = new HashMap<>();
    private Map<String, Marker> stringMarkerMap = new HashMap<>();
    Marker currentMarker;
    Icon defaultIcon = null;
    Icon clickedIcon;


    ArrayList<ShouldWeVenue> ShouldWeVenueList = new ArrayList<>();

    BottomSheetLayout mBottomSheet;

    FloatingActionButton floatingActionButton;
    onFabListener mCallback;

    public interface onFabListener{
        public void onFabClicked(Location location);
    }



    public PlacePickerFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate called");

        IconFactory mIconFactory = IconFactory.getInstance(getActivity());
        Drawable mIconDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_place_grey_24dp);
        clickedIcon = mIconFactory.fromDrawable(mIconDrawable);

        currentUser = ParseUser.getCurrentUser();

        locationServices = LocationServices.getLocationServices(getActivity());
        locationServices.addLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "location changed (oncreate) " + Utils.locationToString(location));


            }
        });
        locationServices.toggleGPS(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_place_picker, container, false);

        mBottomSheet = (BottomSheetLayout) rootView.findViewById(R.id.bottomsheet);
        mBottomSheet.setShouldDimContentView(false);

        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);

        mapView = (MapView) rootView.findViewById(R.id.mapview);
        try{
            mapView.setMyLocationEnabled(false);
            Log.d(TAG, "location enabled");



        }catch (SecurityException e){
            Log.d(TAG, "Could not get location " + e.toString());
        }


        Location l = new Location("Uppsala");
        lastLocation = l;
        l.setLatitude(59.8581);
        l.setLongitude(17.6447);

        if(l != null){
            Log.d(TAG, "Mapview.getmylocation = " + Utils.locationToString(l));
            getParsePlacesNear(l);


        }

        //LatLng loc = Utils.locationToLatLng(l);
        mapView.setStyleUrl(Style.LIGHT);
        mapView.setZoomLevel(11);

        mapView.setCenterCoordinate(new LatLng(59.8581,17.6447));

        /**Location loc = locationServices.getLastLocation();
        try{
            lastLocation = loc;
            mapView.setCenterCoordinate(Utils.locationToLatLng(lastLocation));
            Log.d(TAG, Utils.locationToString(loc));

        }catch (NullPointerException e){
            Log.d(TAG, "no location " + e.toString());
        }**/



        mapView.setOnMarkerClickListener(new MapView.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "Marker clicked");

                Toast.makeText(getActivity(), "Marker: " + marker.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
                com.mapbox.mapboxsdk.camera.CameraPosition cameraPosition = new com.mapbox.mapboxsdk.camera.CameraPosition.Builder()
                        .target(marker.getPosition()).build();

                mapView.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                /**
                 if(marker != currentMarker && currentMarker != null){

                 currentMarker.setIcon(defaultIcon);
                 Log.d(TAG, "seticon defaulticon")
                 }**/


                Log.d(TAG, String.valueOf(mapMarkers.size()));

                Log.d(TAG, "map contains marker: " + String.valueOf(mapMarkers.containsKey(marker)));
                Log.d(TAG, "items in mapmarkers");


                ShouldWeVenue venue = mapMarkers.get(marker.getTitle());
                if (venue != null) {
                    Log.d(TAG, "markerclicked: " + venue.getName());
                    getPlaceDetails(venue);

                }

                //marker.setIcon(clickedIcon);
                Log.d(TAG, "Set icon clickedicon");
                //currentMarker = marker;

                return false; //was false
            }
        });
        mapView.onCreate(savedInstanceState);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "fab clicked", Toast.LENGTH_SHORT).show();


                Log.d(TAG, String.valueOf(lastLocation.getLatitude()));
                mCallback.onFabClicked(lastLocation);

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


        return rootView;


    }

    private ParseGeoPoint geoPointFromLocation(Location loc) {
        return new ParseGeoPoint(loc.getLatitude(), loc.getLongitude());
    }

    public void getParsePlacesNear(Location location){

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

                            ParseGeoPoint pos = venue.getLocation();
                            Marker marker = mapView.addMarker(new MarkerOptions().title(venue.getName()).position(new LatLng(pos.getLatitude(), pos.getLongitude())));
                            Log.d(TAG, marker.getTitle());
                            Log.d(TAG, "added " + venue.getName() + " to map");
                            mapMarkers.put(marker.getTitle(), venue);

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);


    }

    public void updateMarkers(){
        Log.d(TAG, "updateMarkers called");
        for(ShouldWeVenue venue : ShouldWeVenueList){

            ParseGeoPoint pos = venue.getLocation();
            Marker marker = mapView.addMarker(new MarkerOptions().title(venue.getName()).position(new LatLng(pos.getLatitude(), pos.getLongitude())));
            Log.d(TAG, marker.getTitle());
            ShouldWeVenue v = mapMarkers.put(marker.getTitle(), venue);
            Log.d(TAG, "added " + venue.getName() + " to map");
            //stringMarkerMap.put(venue.getObjectId(), marker);
        }
    }

    public void getPlaceDetails(ShouldWeVenue venue){

        String venueName = venue.getName();

        Log.d(TAG, "Get place details called");
        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.place_info, mBottomSheet, false);

        final ArrayList<UserReview> UserReviewList = new ArrayList<>();

        ParseQuery<UserReview> reviews = ParseQuery.getQuery(UserReview.class);
        reviews.whereEqualTo("atVenue", venue);
        reviews.findInBackground(new FindCallback<UserReview>() {
            @Override
            public void done(List<UserReview> objects, ParseException e) {
                if(e == null){
                    Log.d(TAG, "Successfully got review");

                    for(UserReview obj : objects){
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


        CircleImageView circleImageView = (CircleImageView) v.findViewById(R.id.bar_image);
        ParseFile image = venue.getImage();
        Uri imageuri = Uri.parse(image.getUrl());
        Picasso.with(getActivity()).load(imageuri.toString()).fit().centerCrop().into(circleImageView);


        TextView venueTextView = (TextView) v.findViewById(R.id.venueLabelText);
        venueTextView.setText(venueName);



        mBottomSheet.showWithSheetView(v);



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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "we got a new location " + Utils.locationToString(location));

    }
}
