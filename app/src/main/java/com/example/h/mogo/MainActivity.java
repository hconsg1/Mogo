package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFile;
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

public class MainActivity extends Activity implements OnMapReadyCallback {

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
       // getLocation();
        setContentView(R.layout.activity_main);

        // Enable Local Datastore.
        // set parse key and value config
        Log.d("main", "===========================onCreateMain==================");
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");


        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.main_activity_map);
        mapFragment.getMapAsync(this);



        //TODO: THIS BUTTON SHOULD BE THE MAP ITSELF : WHEN USER DRAGS OVER MAP THEN NEW ACTIVITY BY EXPANSION
        ImageButton button = (ImageButton) findViewById(R.id.main_activity_start_camera);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //TODO: start camera preview activity NEEDS TO BE CHANGED
               //start camera preview activity
                Intent video_record_intent = new Intent(MainActivity.this, CameraActivity.class);
                video_record_intent.putExtra("location",long_lat_info_to_grid_info(gpsLocation.getLatitude(),gpsLocation.getLongitude()));

                MainActivity.this.startActivity(video_record_intent);
            }
        });


        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        Log.d("pth", path.getAbsolutePath());


    //    uploadVideo();
        //startCamera();
        //dispatchTakeVideoIntent();


    }//end of oncreate function of main activity
    public String getVideoUrl(ParseObject object){
        ParseFile videoFile = (ParseFile)object.get("firstUpload");
        System.out.println("++++++++++++"+videoFile);
        String myurl = "";
        try {
            myurl = videoFile.getUrl();

        }catch(Exception e){
            System.out.println("parseException");
            System.out.println("===============================ERROR =======================");
            e.printStackTrace();
        }
        return myurl;
    }

    public void get_new_video_feed(String grid_info){
        System.out.println("=======================  starting get new video feed function ============================");
        //TODO: delete everything in the scroll view
        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
        scroll_view.removeAllViews();

        //TODO: get all the video with the same grid index from parse and set the url of the videos to each of them
        List<ParseObject> objectList;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VideoUploadX");

        //TODO: change variable grid_info to actual grid of current location of the user


        query.whereEqualTo("grid_index", grid_info);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    //TODO: SUCCESS
                    Log.d("main","================received Parse Objects======");
                    String url = "";
                    //TODO: loop through query returned objects
                    for (ParseObject object : list) {
                        url = getVideoUrl(object);
                        Log.d("main", getVideoUrl(object));
                        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
                        int width = scroll_view.getWidth();

                        VideoView video = new VideoView(MainActivity.this);
                        video.setVideoPath(url);

                        video.setLayoutParams(new FrameLayout.LayoutParams((width-1), (width-1)));
                        scroll_view.addView(video);
                        MediaController mc = new MediaController(MainActivity.this);
                        video.setMediaController(mc);

                        //TODO: should i do other stuff like focus?
                    }

                    //TODO: turn on video for the very first video
                    //turn_on_video(url);

                } else {
                    //TODO :ERROR
                    Log.d("main", "=================ERROR in getting video from obj================");

                }
            }

        });
    }

//
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
        Log.d("newV","=========================="+new_grid+"=================");

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                System.out.println(data);
         //       Toast.makeText(this, "Image saved to:\n" +
        //                data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                System.out.println(data);
               Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                //TODO: video was captured successfully, get the location and upload file here
                if(gpsLocation != null){
                    //TODO: we at least have the last location
                    uploadVideo();
                }else{
                    //TODO:GSP location is null we fucked
                }

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    public String long_lat_info_to_grid_info(double longitude, double latitude){
        String grid_index;
        //TODO: THIS IS THE MOST IMPORTANT ALGORITHM PART WHERE WE TRANSLATE LONG/ LAT INFO TO GRID LOCATION IN DB
        //HARD CODED FOR NOW
        int x_grid = (int)(longitude* 1000);
        int y_grid = (int)(latitude* 1000);
        grid_index  = Integer.toString(x_grid) + '_' + Integer.toString(y_grid);
        return grid_index;
    }

    //TODO: this funcion needs to be called in camera view activity not here
    public void uploadVideo(){
        if (gpsLocation==null){
            gpsLocation=getLastKnownLocation();
        }
        Double latitude = gpsLocation.getLatitude();
        Double longitude = gpsLocation.getLongitude();


        String grid_index = long_lat_info_to_grid_info(latitude, longitude);

        File filex = new File("/sdcard/VideoB.mp4");
        System.out.println(filex);
        try {
            byte[] byteX = getBytesFromFile(filex);
            ParseFile file = new ParseFile("secondV.mp4", byteX);
            file.saveInBackground();

            ParseObject obj = new ParseObject("VideoUploadX");

            obj.put("firstUpload", file);
            obj.put("grid_index", grid_index);
            obj.saveInBackground();
            Log.d("main", "=======SUCCESSUL FILE UPLOAD!!!!======================");

        }catch (Exception e) {
            e.printStackTrace();
            System.out.print("======error in file upload function in main activity ===============");
        }
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
                current_grid_location = long_lat_info_to_grid_info(gpsLocation.getLongitude(), gpsLocation.getLatitude());
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

        Log.d("tag", "=============map ready==============");
        //System.out.println(boundedList);
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


}//end of main activity class
