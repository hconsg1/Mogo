package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Video_PlayBack_Confirmation_Activity extends Activity {

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
        setContentView(R.layout.video_playback_activity);

        // Enable Local Datastore.
        // set parse key and value config
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");


        //TODO: THIS BUTTON SHOULD BE THE MAP ITSELF : WHEN USER DRAGS OVER MAP THEN NEW ACTIVITY BY EXPANSION
//        ImageButton button = (ImageButton) findViewById(R.id.main_activity_start_camera);
//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                //TODO: start camera preview activity NEEDS TO BE CHANGED
//                //start camera preview activity
//                Intent video_record_intent = new Intent(MainActivity.this, CameraActivity.class);
//                video_record_intent.putExtra("location",long_lat_info_to_grid_info(gpsLocation.getLatitude(),gpsLocation.getLongitude()));
//
//                MainActivity.this.startActivity(video_record_intent);
//            }
//        });


        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);




    }//end of oncreate function of main activity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
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

    //TODO: this funcion needs to be called in camera view activity not here
    public void uploadVideo(){

        //String grid_index = long_lat_info_to_grid_info(latitude, longitude);

        File filex = new File("/sdcard/VideoB.mp4");
        System.out.println(filex);
        try {
            byte[] byteX = getBytesFromFile(filex);
            ParseFile file = new ParseFile("secondV.mp4", byteX);
            file.saveInBackground();

            ParseObject obj = new ParseObject("VideoUploadX");

            obj.put("firstUpload", file);
            //obj.put("grid_index", grid_index);
            obj.saveInBackground();
            Log.d("main", "=======SUCCESSUL FILE UPLOAD!!!!======================");

        }catch (Exception e) {
            e.printStackTrace();
            System.out.print("======error in file upload function in main activity ===============");
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


}//end of main activity class
