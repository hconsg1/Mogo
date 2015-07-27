package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

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
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    private Uri fileUri;
    private Camera mCamera;
    private CameraPreview mPreview;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Enable Local Datastore.
        // set parse key and value config
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");

        Button button = (Button) findViewById(R.id.main_video_view_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MyVideoView.class);
                startActivity(intent);
            }
        });




        dispatchTakeVideoIntent();
    }//end of oncreate function

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("====================== START OF ON ACTRESULT  =======================");
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            try {
                this.getLocation();
                Uri androidUri = data.getData();
                Uri auri = data.getData();
                File filex = new File(getRealPathFromURI(this, auri));

                byte[] byteX = getBytesFromFile(filex);
                ParseFile file = new ParseFile("secondV.mp4", byteX);
                file.saveInBackground();
                TextView tv = (TextView)findViewById(R.id.txtview1);
                tv.setText(androidUri.toString() + "??????" + filex.toString() + "////" +byteX.toString());

                ParseObject obj = new ParseObject("VideoUpload");

                obj.put("firstUpload", file);

                  obj.saveInBackground();

            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("?????????????????????///11111111111111111111111/dfadsifc");
            }
        }
    }

    public void getLocation(){
        final LocationManager locationManager=  (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        final LocationListener locationListener = new LocationListener(){
            public void onLocationChanged(Location location) {
                System.out.println("=======================  starting on location changed ========================");
                location.getLatitude();
                location.getLongitude();
                String myLocation = "Latitude = " + location.getLatitude() + " Longitude = " + location.getLongitude();
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
        //locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

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
/*
    public void getVideofromParse(obj){
        ParseFile applicantResume = (ParseFile)anotherApplication.get("applicantResumeFile");
        applicantResume.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // data has the bytes for the resume
                } else {
                    // something went wrong
                }
            }
        });
    }*/

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


    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

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

}//end of main activity class
