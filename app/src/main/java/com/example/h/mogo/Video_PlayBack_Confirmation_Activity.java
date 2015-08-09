package com.example.h.mogo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Video_PlayBack_Confirmation_Activity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_playback_activity);


        final String path = getIntent().getExtras().getString("file_path");
        final String grid_index = getIntent().getExtras().getString("gridInfo");
        final String geoPoint = getIntent().getExtras().getString("geoPoint");
        Log.d("pb", "========================" + path + "========================");

        VideoView videoView = (VideoView)findViewById(R.id.playback_video_view);
        videoView.setVideoPath("file://" + path);
        videoView.setZOrderOnTop(true);
        MediaController mc = new MediaController(Video_PlayBack_Confirmation_Activity.this);
        videoView.setMediaController(mc);


        ImageButton imageButton = (ImageButton)findViewById(R.id.playback_button_upload);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pb", "=======================OnClick======================");
                uploadVideo(path, grid_index, parseGeoString(geoPoint));
            }
        });
        ImageButton go_back_button = (ImageButton)findViewById(R.id.playback_button_delete);
        go_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Video_PlayBack_Confirmation_Activity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }//end of oncreate function of main activity
    public ParseGeoPoint parseGeoString(String geoString){
        String[] latlong = geoString.split("///");
        return new ParseGeoPoint(Double.parseDouble(latlong[0]),Double.parseDouble(latlong[1]));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }


    //TODO: this funcion needs to be called in camera view activity not here
    public void uploadVideo(String path, String grid_index, ParseGeoPoint geoPoint){
        JSONObject userProfile;
        //TODO: each video upload needs data about the user
         ParseUser currentUser = ParseUser.getCurrentUser();
        userProfile = currentUser.getJSONObject("profile");
/*        if (currentUser.has("profile")) {
            userProfile = currentUser.getJSONObject("profile");
            try {
                if (userProfile.has("facebookId")) {
                    userProfile = userProfile;
                } else {
                    System.out.println("=============USER PROFILE DOES NOT HAVE KEY FACEBOOKID =====================");
                }
            }catch (JSONException e) {
                System.out.println("============= ERROR GETTING USER ID FROM PARSE FACEBOOK BEFORE UPLOADING VIDEO IN VIDEO PLAY BACK ACTIVITY =====================");
                e.printStackTrace();
            }
        }*/

        File filex = new File(path);
        System.out.println(filex);
        try {
            byte[] byteX = getBytesFromFile(filex);
            ParseFile file = new ParseFile("forthV.mp4", byteX);
            file.saveInBackground();

            ParseObject obj = new ParseObject("VideoUploadX");

            obj.put("firstUpload", file);
            obj.put("grid_index", grid_index);
            obj.put("geoPoint", geoPoint);
            obj.put("creator_id", userProfile.toString());
            obj.put("num_like",0);

            Log.d("main", "=======SUCCESSUL FILE UPLOAD!!!!======================");
            //obj.put("creator_id", user_id);

            obj.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    Log.d("v","========================DONE!!!===================");
                    if (e == null) {
                        Log.d("v","======================SUCCESSFUL===================");
                        Context context = getApplicationContext();
                        CharSequence text = "video uploaded";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } else {

                        Context context = getApplicationContext();
                        CharSequence text = "Failed to upload video please try later";
                        System.out.println("=====================UPLOAD FAILED!!!===============");
                        e.printStackTrace();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }

            });




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
