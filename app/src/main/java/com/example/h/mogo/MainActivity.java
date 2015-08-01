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
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity  {

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
    private Uri fileUri;
    private Camera mCamera;
    private CameraPreview mPreview;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    LocationManager locationManager;
    Location gpsLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // Enable Local Datastore.
        // set parse key and value config
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");

        //start updating current location
        getLocation();
        Button button = (Button) findViewById(R.id.main_video_view_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapView.class);
                startActivity(intent);
            }
        });
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        Log.d("pth", path.getAbsolutePath());
       // uploadVideo();
      //  startCamera();



   //     dispatchTakeVideoIntent();
    }//end of oncreate function

//TODO: 현창아 우리 계속 location update하려면 나중에 이코드도 있어야함
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopLocationUpdates();
//    }
//
//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, this);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
//    }



    private void startCamera(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);  // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name

     //   intent.putExtra(MediaStore.EXTRA__QUALITY, 1); // set the video image quality to high

        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
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
        Log.e("tg", "==========================================");
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
        System.out.println("==================="+requestCode);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Log.d("tag","@22222222222222");
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
                System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println(data);
               Toast.makeText(this, "Video saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
                //TODO: video was captured successfully, get the location and upload file here
                if(gpsLocation != null){
                    //TODO: we at least have the last location
                    uploadVideo(gpsLocation);
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
        String grid_index = '';
        //TODO: THIS IS THE MOST IMPORTANT ALGORITHM PART WHERE WE TRANSLATE LONG/ LAT INFO TO GRID LOCATION IN DB
        //HARD CODED FOR NOW
        grid_index = "11_11";
        return grid_index;
    }

    public void uploadVideo(Location mylocation){

        Double longitude = mylocation.getLatitude();
        Double latitude = mylocation.getLatitude();
        longitude = 11.0;
        latitude = 11.0;
        String grid_index = long_lat_info_to_grid_info(longitude, latitude);
        
        File filex = new File("/sdcard/DCIM/Camera/VID_20150728_211002.mp4");
        Log.d("tag", "@@@@@@@@@@@@@@@@@@@@@@@@@2");
        System.out.println(filex);
        try {
            byte[] byteX = getBytesFromFile(filex);
            ParseFile file = new ParseFile("secondV.mp4", byteX);
            file.saveInBackground();

            ParseObject obj = new ParseObject("VideoUploadX");

            obj.put("firstUpload", file);
            obj.put("grid_index", grid_index);
            obj.saveInBackground();

        }catch (Exception e) {
            e.printStackTrace();
            System.out.print("?????????????????????///11111111111111111111111/");
        }

    }


   // @Override
   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("====================== START OF ON ACTRESULT  =======================");
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            try {
                locationManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                this.getLocation();

                Uri androidUri = data.getData();
                Uri auri = data.getData();
                File filex = new File(getRealPathFromURI(this, auri));
                Log.d("tag", "@@@@@@@@@@@@@@@@@@@@@@@@@2");
                System.out.println(filex);
                byte[] byteX = getBytesFromFile(filex);
                ParseFile file = new ParseFile("secondV.jpg", byteX);
                file.saveInBackground();

                ParseObject obj = new ParseObject("VideoUpload");

                if (gpsLocation == null){
                    gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Log.d("tag", gpsLocation.toString());
                }

                obj.put("location", gpsLocation.toString());
                obj.put("firstUpload", file);

                  obj.saveInBackground();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("?????????????????????///11111111111111111111111/dfadsifc");
            }
        }
    }
*/
    public void getLocation(){

        final LocationListener locationListener = new LocationListener(){
            public void onLocationChanged(Location location) {
                System.out.println("=======================  starting on location changed ========================");
                gpsLocation = location;
                //I make a log to see the results
                Log.e("MY CURRENT LOCATION", myLocation);

            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}
            @Override
            public void onProviderEnabled(String s) {}
            @Override
            public void onProviderDisabled(String s) {}
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationistener);


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


}//end of main activity class
