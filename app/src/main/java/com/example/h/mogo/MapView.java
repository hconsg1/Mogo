package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class MapView extends Activity {

    private com.mapbox.mapboxsdk.views.MapView mapview_map;
    private String currentMap = null;
    ArrayList<Marker> _visibleMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview_activity);
        //Custom Map
        mapview_map = (com.mapbox.mapboxsdk.views.MapView) findViewById(R.id.custommapview);
        mapview_map.setMinZoomLevel(mapview_map.getTileProvider().getMinimumZoomLevel());
        mapview_map.setMaxZoomLevel(mapview_map.getTileProvider().getMaximumZoomLevel());
        mapview_map.setCenter(mapview_map.getTileProvider().getCenterCoordinate());
        mapview_map.setZoom(0);
        currentMap = getString(R.string.streetMapId);

        // Show user location (purposely not in follow mode)
        mapview_map.setUserLocationEnabled(true);

        mapview_map.loadFromGeoJSONURL("https://gist.githubusercontent.com/tmcw/10307131/raw/21c0a20312a2833afeee3b46028c3ed0e9756d4c/map.geojson");

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





}//end of class