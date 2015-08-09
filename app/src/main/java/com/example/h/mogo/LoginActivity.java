package com.example.h.mogo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        LoginButton fb_button = (LoginButton)findViewById(R.id.facebook_login_button);
        fb_button.setReadPermissions(Arrays.asList("email"));
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClick(v);
            }
        });
        // Check if there is a currently logged in user
        // and it's linked to a Facebook account.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Go to the user info activity
            showMainActivity();
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginClick(View v) {
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "user_friends", "email");

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    //Log.d(IntegratingFacebookTutorialApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
                    System.out.println("================ USER IS NULL  =====================================");
                } else if (user.isNew()) {
                    //Log.d(IntegratingFacebookTutorialApplication.TAG, "User signed up and logged in through Facebook!");
                    System.out.println("================ NEW USER   LOGGED IN !!!!!!!!! =====================================");

                    showMainActivity();
                } else {
                    //Log.d(IntegratingFacebookTutorialApplication.TAG, "User logged in through Facebook!");
                    System.out.println("================ LOGGED IN !!!!!!!!! =====================================");

                    showMainActivity();
                }
            }
        });
    }//end of login click

    private void makeMeRequest() {
        Log.d("vp", "======================makeMeRequest======================");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        System.out.println("=====================onCompletedMakeMeRequest");

                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                System.out.println("\n\n\n000000000000000000000000000000000000000000000\n\n\n");
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));
                                System.out.println("\n\n\n111111111111111111111111111111111111\n\n\n");


                                if (jsonObject.has("email")  &&   jsonObject.getString("email") != null) {
                                    userProfile.put("email", jsonObject.getString("email"));
                                }
                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                            } catch (JSONException e) {
                                System.out.println("\n\n\n\n\n\n\n\n\n\n\n============ ERROR WHEN GETTING USER PROFILE IN LOGIN ACTIVITY ========================");
                                e.printStackTrace();
                            }

                        } else if (graphResponse.getError() != null) {
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d("vp",
                                            "Authentication error: " + graphResponse.getError());
                                    break;

                                case TRANSIENT:
                                    Log.d("vp",
                                            "Transient error. Try again. " + graphResponse.getError());
                                    break;

                                case OTHER:
                                    Log.d("vp",
                                            "Some other error: " + graphResponse.getError());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();

    }
    private void showMainActivity() {
        makeMeRequest();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}