package com.example.gustaftegner.shouldwetest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by gustaftegner on 24/12/15.
 */



public class MapBoxFragment extends Fragment {

   private BottomSheetLayout mBottomSheet;
    private MapView mapView = null;
/**

    @Override
    public void onCreate(Bundle savedInstanceState){


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.mapbox_fragment, container, false);

        mBottomSheet = (BottomSheetLayout) v.findViewById(R.id.bottomsheet);
        mBottomSheet.setShouldDimContentView(false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.setMyLocationEnabled(true);

        mapView.setStyleUrl(Style.LIGHT);
        mapView.setCenterCoordinate(new LatLng(59.329323, 18.068581));
        mapView.setZoomLevel(11);

        mapView.onCreate(savedInstanceState);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause()  {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
    **/

}
