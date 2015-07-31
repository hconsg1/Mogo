package com.example.h.mogo;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MapView extends Activity implements OnMapReadyCallback {

    GoogleMap _map;
    ArrayList<Marker> _visibleMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_activity);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
        Button button= (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapView.this, VideoList.class);
              //  intent.putExtra("markerList",_visibleMarkers );
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        _map = map;
        final ArrayList<Marker> markerArray = setMarker();
        LatLng currentLoc = new LatLng(41.8262, -71.4032);
        ArrayList<Marker> boundedList = getBoundedMarkers(markerArray);

        _map.setMyLocationEnabled(true);
        _map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));
        _map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("m","=====================camera move=========");
                System.out.println(getBoundedMarkers(markerArray));
            }
        });


        Log.d("tag","=============map ready==============");



        System.out.println(boundedList);
    }

    public ArrayList<Marker> getBoundedMarkers(ArrayList<Marker> markerArray){
        ArrayList<Marker> markerList = new ArrayList<>();
        for (Marker marker : markerArray){
            if (isVisibleOnMap(marker.getPosition())){
                markerList.add(marker);
            }
        }
        _visibleMarkers = markerList;
        return markerList;


    }

    public boolean isVisibleOnMap(LatLng latLng) {
        VisibleRegion vr = _map.getProjection().getVisibleRegion();
        return vr.latLngBounds.contains(latLng);
    }

    public ArrayList<Marker> setMarker(){
        //setMarkers , return Marker array
        LatLng loc1 = new LatLng(41.8262, -71.4034);
        LatLng loc2 = new LatLng(41.8258, -71.4078);
        Marker marker1 = _map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(loc1));

        Marker marker2 = _map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(loc2));
        _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapView.this, MyVideoView.class);
                startActivity(intent);
                return false;
            }
        });

        ArrayList<Marker> markerList = new ArrayList<Marker>();

        markerList.add(marker1);
        markerList.add(marker2);
        return markerList;
    }




}