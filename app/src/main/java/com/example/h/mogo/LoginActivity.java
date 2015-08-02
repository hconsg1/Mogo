package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

//import android.support.v4.app.Fragment;

public class LoginActivity extends FragmentActivity {

    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;
    private Activity myactivity = this;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(myactivity.getApplicationContext());
        setContentView(R.layout.login_activity);
        callbackManager = CallbackManager.Factory.create();
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken old_accessToken, AccessToken new_accessToken) {

            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile old_profile, Profile new_profile) {
            //TODO: do something with the profile
            }
        };

        LoginButton loginbutton = (LoginButton)findViewById(R.id.faceook_login_button);
        loginbutton.setReadPermissions("user_friends");
        loginbutton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                System.out.println("===================FACEBOOK LOGIN SUCCESSS ===========================================");
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.e("er","=======================Facebook LOGIN  ERROR ===================================");
            }
        });
        mTokenTracker.startTracking();
        mProfileTracker.startTracking();

    }//end of on create


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        return inflater.inflate(R.layout.login_activity, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState){
//        super.onViewCreated(view, savedInstanceState);
//        LoginButton loginbutton = (LoginButton) view.findViewById(R.id.faceook_login_button);
//        loginbutton.setReadPermissions("user_friends");
//        loginbutton.setFragment(this);
//        loginbutton.registerCallback(mCallbackManager, mCallback);
//
//    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume(){
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        Log.d("tag", "=========================on Resume FROM LOGIN =================================================");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        //TODO: do something with the profile

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.d("tag", "=========================onActivtyResult FROM LOGIN ACTITIVY ================================");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("tag", "=========================on Start FROM LOGIN ===========================================");
    }

    @Override
    public void onStop() {
        Log.d("tag","=========================on Stop FROM LOGIN ====================================================");
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("tag", "=========================on Destroy");
        mTokenTracker.stopTracking();
    }

}