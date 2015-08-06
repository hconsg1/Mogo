package com.example.h.mogo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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
        progressDialog = ProgressDialog.show(LoginActivity.this, "", "Logging in...", true);

        List<String> permissions = Arrays.asList("public_profile", "email");
        // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
        // (https://developers.facebook.com/docs/facebook-login/permissions/)

        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                progressDialog.dismiss();
                if (user == null) {
                    //Log.d(IntegratingFacebookTutorialApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
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

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}