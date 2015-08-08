package com.example.h.mogo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        fb_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("pb", "=======================OnClick======================");
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
        System.out.println("000000000000000000000000000000000000000000000");
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        System.out.println("111111111111111111111111111111111111111111111");
    }

    public void onLoginClick(View v) {
        System.out.println("\n\n\n\n\n\n\n2222222222222222222222222222222222222222222222222222222222222222222222222222");
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "user_friends");

        System.out.println("\n\n\n\n\n\n\n3333333333333333333333333333333333333333333333333333333333333333");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                System.out.println("\n\n\n\n\n\n\n44444444444444444444444444444444444444444444444444444");
                progressDialog.dismiss();
                System.out.println("\n\n\n\n\n\n\n44444444444444444444444444444444444444444444444444444  AFTER PROCESS DISMISS FUNCION   ");
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
        System.out.println("\n\n\n\n\n\n\n5555555555555555555555555555555555555555555555555555555555555555555555");
    }//end of login click
    private void makeMeRequest() {
        Log.d("vp", "======================makeMeRequest======================");
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        if (jsonObject != null) {
                            JSONObject userProfile = new JSONObject();

                            try {
                                userProfile.put("facebookId", jsonObject.getLong("id"));
                                userProfile.put("name", jsonObject.getString("name"));


                                /*if (jsonObject.getString("gender") != null)
                                    userProfile.put("gender", jsonObject.getString("gender"));

                                if (jsonObject.getString("email") != null)
                                    userProfile.put("email", jsonObject.getString("email"));
*/
                                // Save the user profile info in a user property
                                ParseUser currentUser = ParseUser.getCurrentUser();
                                currentUser.put("profile", userProfile);
                                currentUser.saveInBackground();

                                // Show the user info
                                // user_id = userProfile.getString("facebookId");
                                Log.d("1","======================="+currentUser+"==========");
                                Log.d("1","======================="+userProfile+"==========");
                            } catch (JSONException e) {
                                Log.d("vp",
                                        "Error parsing returned user data. " + e);
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

        request.executeAsync();
    }
    private void showMainActivity() {
        makeMeRequest();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}