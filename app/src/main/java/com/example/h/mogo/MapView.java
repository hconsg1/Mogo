package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MapView extends Activity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mapview_map;
    private Location gpsLocation;
    ArrayList<Marker> _visibleMarkers;
    private double mylat;
    private double mylong;
    private View popupView;
    private PopupWindow popupWindow;
    private boolean popup_show = false;
    private View map_fragment_element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

         mylat = this.getIntent().getExtras().getDouble("lat");
         mylong  = this.getIntent().getExtras().getDouble("lon");
        setContentView(R.layout.mapview_activity);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapview_map);
        mapFragment.getMapAsync(this);
        map_fragment_element =  findViewById(R.id.mapview_map);

        ImageButton back_button= (ImageButton) findViewById(R.id.mapview_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapView.this, MainActivity.class);
                //  intent.putExtra("markerList",_visibleMarkers );
                startActivity(intent);
            }
        });
    }//end of on create


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapview_map = googleMap;

        mapview_map.setOnMapLongClickListener(this);
        //final ArrayList<Marker> markerArray = setMarker();
        //ArrayList<Marker> boundedList = getBoundedMarkers(markerArray);

        LatLng currentLoc = new LatLng(mylat, mylong);

        mapview_map.setMyLocationEnabled(true);
        mapview_map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));
        mapview_map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.d("m","=====================camera move=====================");
//                System.out.println(getBoundedMarkers(markerArray));
            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //TODO: popup with 2 options

        // if popup of already showing then close and create new one
        if(popup_show){
            popupWindow.dismiss();
            popup_show = false;
        }

        //create new one popup
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.mapview_popup, null);

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(map_fragment_element, Gravity.NO_GRAVITY, 0, 0 );
        popup_show = true;


    }
}//end of class