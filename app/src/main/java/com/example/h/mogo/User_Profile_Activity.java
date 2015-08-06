package com.example.h.mogo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class User_Profile_Activity extends Activity {

    private ProfilePictureView userProfilePictureView;
    private TextView userNameView;
    private TextView userGenderView;
    private TextView userEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

//        userProfilePictureView = (ProfilePictureView) findViewById(R.id.userProfilePicture);
//        userNameView = (TextView) findViewById(R.id.userName);
//        userGenderView = (TextView) findViewById(R.id.userGender);
//        userEmailView = (TextView) findViewById(R.id.userEmail);


        //Fetch Facebook user info if it is logged
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && currentUser.isAuthenticated()) {
            makeMeRequest();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Check if the user is currently logged
            // and show any cached content
            updateViewsWithProfileInfo();
        } else {
            // If the user is not logged in, go to the
            // activity showing the login view.
            startLoginActivity();
        }
    }

    private void makeMeRequest() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));

                                if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));

                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                updateViewsWithProfileInfo();
                            } catch (JSONException e) {

                            }
                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    //TODO : authentication error

                                    break;

                                case TRANSIENT:
                                    //TODO: transient error
                                    break;

                                case OTHER:
                                    //TODO : other error
                                    break;
                            }
                        }
                    }
                });

        request.executeAsync();
    }

    private void updateViewsWithProfileInfo() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser.has("profile")) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {

                if (userProfile.has("facebookId")) {
                    userProfilePictureView.setProfileId(userProfile.getString("facebookId"));
                } else {
                    // Show the default, blank user profile picture
                    userProfilePictureView.setProfileId(null);
                }

                if (userProfile.has("name")) {
                    userNameView.setText(userProfile.getString("name"));
                } else {
                    userNameView.setText("");
                }

                if (userProfile.has("gender")) {
                    userGenderView.setText(userProfile.getString("gender"));
                } else {
                    userGenderView.setText("");
                }

                if (userProfile.has("email")) {
                    userEmailView.setText(userProfile.getString("email"));
                } else {
                    userEmailView.setText("");
                }

            } catch (JSONException e) {
                //Log.d(IntegratingFacebookTutorialApplication.TAG, "Error parsing saved user data.");
            }
        }
    }

    public void onLogoutClick(View v) {
        logout();
    }

    private void logout() {
        // Log the user out
        ParseUser.logOut();

        // Go to the login view
        startLoginActivity();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void get_user_video_feed(String grid_info){
        System.out.println("=======================  starting get new video feed function ============================");
        //TODO: delete everything in the scroll view
        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
        scroll_view.removeAllViews();

        //TODO: get all the video with the same grid index from parse and set the url of the videos to each of them
        List<ParseObject> objectList;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VideoUploadX");

        //TODO: change variable grid_info to actual grid of current location of the user

        String creator_id = "1";
        query.whereEqualTo("creator_id", creator_id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    //TODO: SUCCESS
                    Log.d("main", "================received Parse Objects======");
                    String url = "";
                    //TODO: loop through query returned objects
                    for (ParseObject object : list) {
                        url = getVideoUrl(object);
                        Log.d("main", getVideoUrl(object));
                        LinearLayout scroll_view = (LinearLayout) findViewById(R.id.main_activity_video_scrollView_wrapper);
                        int width = scroll_view.getWidth();

                        VideoView video = new VideoView(User_Profile_Activity.this);
                        video.setVideoPath(url);

                        video.setLayoutParams(new FrameLayout.LayoutParams((width - 1), (width - 1)));
                        scroll_view.addView(video);
                        MediaController mc = new MediaController(User_Profile_Activity.this);
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



}//end of class