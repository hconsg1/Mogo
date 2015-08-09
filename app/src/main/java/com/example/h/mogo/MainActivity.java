package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;


import android.widget.Button;
import android.widget.EditText;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends Activity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    public static final String LOGTAG = "VIDEOCAPTURE";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private String myLocation;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private LocationListener locationListener;
    private Uri fileUri;
    GoogleMap main_activity_map;
    ArrayList<Marker> mapMarkers;
    private CameraPreview mPreview;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    LocationManager locationManager;
    Location gpsLocation;
    private String current_grid_location;
    final private String venmo_app_secret  = "uAQP3LkE8YENxbCnkdgxEjq73rwTkxLM";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        setContentView(R.layout.activity_main);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_activity_map);
        mapFragment.getMapAsync(this);



        //TODO: THIS BUTTON SHOULD BE THE MAP ITSELF : WHEN USER DRAGS OVER MAP THEN NEW ACTIVITY BY EXPANSION
        ImageButton button = (ImageButton) findViewById(R.id.main_activity_start_camera);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //TODO: start camera preview activity NEEDS TO BE CHANGED
                //start camera preview activity
                Intent video_record_intent = new Intent(MainActivity.this, CameraActivity.class);

                video_record_intent.putExtra("location", long_lat_info_to_grid_info(gpsLocation.getLatitude(), gpsLocation.getLongitude()));
                video_record_intent.putExtra("geoPoint", gpsLocation.getLatitude() + "///" + gpsLocation.getLongitude());


                MainActivity.this.startActivity(video_record_intent);

                overridePendingTransition(R.anim.animation_push_up_in, R.anim.animation_push_up_out);
            }
        });



        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);


        //uploadVideo();
        //startCamera();


        //Button  Open Notification
        ImageButton notibutton = (ImageButton) findViewById(R.id.main_activity_start_notification);
        notibutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent open_notification_intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(open_notification_intent);
                overridePendingTransition(R.anim.animation_push_left_in, R.anim.animation_push_left_out);
            }
        });


        //Button   Open Settings
        ImageButton settingsbutton = (ImageButton) findViewById(R.id.main_activity_start_settings);
        settingsbutton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent open_settings_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(open_settings_intent);
                overridePendingTransition(R.anim.animation_push_right_in, R.anim.animation_push_right_out);
            }
        });

        //Tab Hot and New
        final Button tab_button_new = (Button) findViewById(R.id.main_activity_tab_new);
        final Button tab_button_hot = (Button) findViewById(R.id.main_activity_tab_hot);
        tab_button_new.setEnabled(true);
        if(tab_button_new.isEnabled()) {
            tab_button_hot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tab_button_hot.setEnabled(true);
                    tab_button_new.setEnabled(false);
                    tab_button_hot.setBackgroundColor(Color.rgb(252, 78, 94));
                    tab_button_hot.setTextColor(Color.WHITE);
                    tab_button_new.setBackgroundColor(Color.WHITE);
                    tab_button_new.setTextColor(Color.rgb(184,184,184));
                }
            });
        }
        else
        {
            tab_button_new.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tab_button_hot.setEnabled(false);
                    tab_button_new.setEnabled(true);
                    tab_button_hot.setBackgroundColor(Color.WHITE);
                    tab_button_hot.setTextColor(Color.rgb(184, 184, 184));
                    tab_button_new.setBackgroundColor(Color.rgb(252, 78, 94));
                    tab_button_new.setTextColor(Color.WHITE);
                }
            });
        }
        //dispatchTakeVideoIntent();


    }//end of oncreate function of main activity





    public void addLike(String objectId){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("videoUploadX");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    int num_likes = object.getInt("num_like");
                    object.put("num_like", num_likes++);
                    object.saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }


    public String getVideoUrl(ParseObject object){
        ParseFile videoFile = (ParseFile)object.get("firstUpload");
        System.out.println("++++++++++++"+videoFile);
        String myurl = "";
        try {
            myurl = videoFile.getUrl();

        }catch(Exception e){
            System.out.println("===============================ERROR IN GET VIDEO URL =======================");
            e.printStackTrace();
        }
        return myurl;
    }

    //TODO: get which button HOT OR NEW is clicked so that we can figure out which videos to put into the feed
    public String  get_hot_or_new(){

        //        if(  ){
        //            return "hot";
        //        }else{
        //            return "new";
        //        }


        return "hot";
    }


    //TODO: need to put LIMIT on the number of video being fetched. ALSO LOAD MORE VIDEO functionality has to be implemented
    public void get_new_video_feed(String grid_info){
        System.out.println("=======================  starting get new video feed function ============================");

        //TODO: delete everything in the scroll view
        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
        scroll_view.removeAllViews();

        //TODO: get all the video with the same grid index from parse and set the url of the videos to each of them
        List<ParseObject> objectList;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VideoUploadX");
        query.whereEqualTo("grid_index", grid_info);

        if(get_hot_or_new() == "new"){
            query.orderByAscending("createdAt");
        }else{
            query.orderByAscending("num_like");
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    //TODO: SUCCESS
                    String url = "";
                    //TODO: loop through query returned objects
                    for (ParseObject object : list) {
                        url = getVideoUrl(object);
                        Log.d("main", getVideoUrl(object));
                        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
                        int width = scroll_view.getWidth();

                        VideoView video = new VideoView(MainActivity.this);

                        //set tag for each appended videos => the tags are EQUAL to object ID in DATABASE
                        String video_object_id = object.getObjectId();
                        ParseGeoPoint video_location = object.getParseGeoPoint("geoPoint");
                        ArrayList<Object> array_object = new ArrayList<Object>();
                        array_object.add(video_object_id);
                        array_object.add(video_location);
                        video.setTag(array_object);
                        MediaController mc = new MediaController(MainActivity.this);
                        video.setMediaController(mc);
                        video.setVideoPath(url);
                        mc.setMinimumWidth(video.getMeasuredWidth());
                        mc.setAnchorView(video);


                        video.setLayoutParams(new FrameLayout.LayoutParams((width - 1), (width - 1)));


                        HorizontalScrollView horScroll = new HorizontalScrollView(MainActivity.this);
                        RelativeLayout relativeLayout = new RelativeLayout(MainActivity.this);
                        relativeLayout.setMinimumWidth(300);
                        relativeLayout.setBackgroundColor(Color.RED);
                        relativeLayout.setMinimumHeight(video.getMeasuredHeight());
                        LinearLayout topLinearLayout = new LinearLayout(MainActivity.this);
                        // topLinearLayout.setLayoutParams(android.widget.LinearLayout.LayoutParams.FILL_PARENT,android.widget.LinearLayout.LayoutParams.FILL_PARENT);
                        topLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        topLinearLayout.addView(video);
                        topLinearLayout.addView(relativeLayout);


                        horScroll.addView(topLinearLayout);
                        scroll_view.addView(horScroll);





                        //TODO: should i do other stuff like focus?
                    }

                    //TODO: turn on video for the very first video or turn on the video that is in certain part of the screen
                    //turn_on_video(url);

                } else {
                    //TODO :ERROR
                    Log.d("main", "=================ERROR in getting video from obj================");

                }
            }

        });
    }


//    public void turn_on_video(String uri){
//        System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//        Log.d("main", "Turn On Video============================================");
//        Log.d("main", uri);
//        VideoView vd = (VideoView)findViewById(R.id.VideoView1);
//        MediaController mc = new MediaController(MainActivity.this);
//        vd.setMediaController(mc);
//        vd.requestFocus();
//
//        vd.setVideoPath(uri);
//        vd.requestFocus();
//        vd.start();
//    }


    @Override
    public void onMapLongClick(LatLng point) {

        Intent intent = new Intent(MainActivity.this, MapView.class);
        intent.putExtra("lat", gpsLocation.getLatitude());
        intent.putExtra("lon", gpsLocation.getLongitude());
        startActivity(intent);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public boolean need_new_video_feed(){
        gpsLocation = getLastKnownLocation();
        Double current_long = gpsLocation.getLongitude();
        Double current_lat = gpsLocation.getLatitude();
        String new_grid = long_lat_info_to_grid_info(current_lat, current_long);

        if(current_grid_location == new_grid){
            return false;
        }else{
            return true;
        }
    }

    private Location getLastKnownLocation() {
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
        locationListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLocation();
        if(need_new_video_feed() == true){
            String current_grid_index = long_lat_info_to_grid_info(gpsLocation.getLatitude(), gpsLocation.getLongitude());
            get_new_video_feed(current_grid_index);
        }
    }



    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        Log.d("tag",Environment.getExternalStorageState());
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
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



    public void getLocation(){
        gpsLocation = getLastKnownLocation();
        locationListener = new LocationListener(){

            @Override
            public void onLocationChanged(Location location) {
                System.out.println("=======================  starting on location changed ========================");
                gpsLocation = location;
                //I make a log to see the results
                Log.e("MY CURRENT LOCATION", gpsLocation.toString());
                current_grid_location = long_lat_info_to_grid_info(gpsLocation.getLatitude(), gpsLocation.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 100, locationListener);


    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            System.out.print(cursor.getString(column_index));
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        main_activity_map = map;

        main_activity_map.setOnMapLongClickListener(this);
        //final ArrayList<Marker> markerArray = setMarker();
        //ArrayList<Marker> boundedList = getBoundedMarkers(markerArray);
        double mylong = gpsLocation.getLongitude();
        double mylat = gpsLocation.getLatitude();
        LatLng currentLoc = new LatLng(mylat, mylong);
        String grid_location = long_lat_info_to_grid_info(mylat, mylong);
        current_grid_location = grid_location;
       // get_new_video_feed(grid_location);

        main_activity_map.setMyLocationEnabled(true);
        main_activity_map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 13));
        main_activity_map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                Log.d("m","=====================camera move=====================");
//                System.out.println(getBoundedMarkers(markerArray));
            }
        });

    }



//
//    public ArrayList<Marker> getBoundedMarkers(ArrayList<Marker> markerArray){
//        ArrayList<Marker> markerList = new ArrayList<>();
//        for (Marker marker : markerArray){
//            if (isVisibleOnMap(marker.getPosition())){
//                markerList.add(marker);
//            }
//        }
//        mapMarkers = markerList;
//        return markerList;
//
//
//    }
//
//    public boolean isVisibleOnMap(LatLng latLng) {
//        VisibleRegion vr = main_activity_map.getProjection().getVisibleRegion();
//        return vr.latLngBounds.contains(latLng);
//    }

//    public ArrayList<Marker> setMarker(){
//        //setMarkers , return Marker array
//        Log.d("tag","===================set Marker==============================");
//        LatLng loc1 = new LatLng(gpsLocation.getLatitude(), gpsLocation.getLongitude());
//        Log.d("tag",loc1.toString());
//        Marker marker1 = main_activity_map.addMarker(new MarkerOptions()
//                .title("my current location")
//                .snippet("click this button to show all videos around you!")
//                .position(loc1));
//
//        main_activity_map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Intent intent = new Intent(MainActivity.this, MyVideoView.class);
//                startActivity(intent);
//                return false;
//            }
//        });
//
//        ArrayList<Marker> markerList = new ArrayList<Marker>();
//        markerList.add(marker1);
//        return markerList;
//    }
//
//    public ArrayList<Marker> setExampleMarkers(){
//
//        return new ArrayList<Marker>();
//    }



    //need to be set as a part of onclick listener for pay for video in main activity
    public void start_payment_activity(String recipient_info, String amount, String note){

        Intent venmoIntent = VenmoLibrary.openVenmoPayment("2843", "Mogo", recipient_info, amount, note, "pay");
        startActivityForResult(venmoIntent, 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case 1: {
                if(resultCode == RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoLibrary.VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, venmo_app_secret);
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                        }
                    }
                    else {
                        String error_message = data.getStringExtra("error_message");
                        //An error ocurred.  Make sure to display the error_message to the user
                    }
                }
                else if(resultCode == RESULT_CANCELED) {
                    //The user cancelled the payment
                }
                break;
            }
        }
    }//end of on result


}//end of main activity class
