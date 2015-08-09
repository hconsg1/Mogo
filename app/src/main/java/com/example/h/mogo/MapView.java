package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

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
        final Double lat = latLng.latitude;
        final Double lon = latLng.longitude;
        if(popup_show){
            popupWindow.dismiss();
            popup_show = false;
        }

        //create new one popup
        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.mapview_popup, null);

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.showAtLocation(map_fragment_element, Gravity.NO_GRAVITY, 0, 0);
        popup_show = true;

        mapview_map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("my current location")
                .snippet("click this button to show all videos around you!")
                .position(new LatLng(lat, lon)));

        Button new_location_button = (Button) popupView.findViewById(R.id.mapview_go_to_main_feed);

        new_location_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("==============OnCLick====================");
                Intent intent = new Intent(MapView.this, MainActivity.class);
                intent.putExtra("new_lat", lat);
                intent.putExtra("new_lon", lon);
                startActivity(intent);
            }
        });

        Button request_button = (Button) popupView.findViewById(R.id.mapview_popup_request);
        request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ParseObject obj = new ParseObject("request_table");


                ParseUser currentUser = ParseUser.getCurrentUser();
                JSONObject userProfile = currentUser.getJSONObject("profile");

                obj.put("requested_user", userProfile.toString());
                obj.put("grid_index", long_lat_info_to_grid_info(lat, lon));
                obj.put("location", new ParseGeoPoint(lat,lon));
                obj.saveInBackground();
                Context context = getApplicationContext();
                CharSequence text = "Video Requested!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                toast.show();
            }
        });
 /*       EditText editText = (EditText) popupView.findViewById(R.id.editText);
        editText.setFocusable(true);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);                }
            }
        });*/
    }

    public String long_lat_info_to_grid_info(double latitude , double longitude){
        String grid_index;
        //TODO: THIS IS THE MOST IMPORTANT ALGORITHM PART WHERE WE TRANSLATE LONG/ LAT INFO TO GRID LOCATION IN DB
        //HARD CODED FOR NOW
        int x_grid = (int)(longitude* 1000);
        int y_grid = (int)(latitude* 1000);
        grid_index  = Integer.toString(x_grid) + '_' + Integer.toString(y_grid);
        return grid_index;
    }
}//end of class