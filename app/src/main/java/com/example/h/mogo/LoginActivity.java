package com.example.h.mogo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;

public class LoginActivity extends Fragment {

    String fbUserImg;
    String fbUserFirstName;
    String fbUserFullName;
    String fbUserGender;
    String fbUserEmail;

    private ProfileTracker mProfileTracker;
    AccessToken accessToken;
    AccessTokenTracker accessTokenTracker;


    private CallbackManager mCallBackManager;
    private FacebookCallback<LoginResult> mCallBack=new FacebookCallback<LoginResult>() {


        @Override
        public void onSuccess(LoginResult loginResult) {

            accessToken = loginResult.getAccessToken();

            GraphRequest request  = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {

                    fbUserFirstName = object.optString("first_name");
                    fbUserFullName = object.optString("name");
                    fbUserEmail = object.optString("email");
                    fbUserGender = object.optString("gender");

                    Log.e("Full Details", object.toString());
                    Log.e("First Name", fbUserFirstName);
                    Log.e("Full Name", fbUserFullName);
                    Log.e("Email", fbUserEmail);
                    Log.e("Gender", fbUserGender);

                    SharedPreferences lUserDb = getActivity().getSharedPreferences("localUserDb", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = lUserDb.edit();

                    editor.putString("firstName", fbUserFirstName);
                    editor.putString("fullName", fbUserFullName);
                    editor.putString("email", fbUserEmail);
                    editor.putString("gender", fbUserGender);

                    editor.commit();

                }
            });  request.executeAsync();

            Bundle parameters = new Bundle();
            parameters.putString("", "id,name,email,gender,locale,picture.width(300)");
            request.setParameters(parameters);

            mProfileTracker = new ProfileTracker() {

                @Override
                protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                    if(profile2 != null) {
                        fbUserImg = profile2.getProfilePictureUri(160, 160).toString();
                        mProfileTracker.stopTracking();

                        if (fbUserImg != null) {

                            SharedPreferences uFbData = getActivity().getSharedPreferences("UFD", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = uFbData.edit();

                            editor.putString("imgUrl", fbUserImg);
                            Log.e("Profile Image", fbUserImg);
                            editor.commit();

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }

                    } else {

                        Log.e("YOU", "FUCKED UP NIGGA, ITS NULL");
                    }
                }
            };

        }


        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    public LoginActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mCallBackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
            }
        };

        if (accessToken != null) {
            // If the access token is available already assign it.
            accessToken = AccessToken.getCurrentAccessToken();
            Log.e("NEW ACCESS TOKEN:", accessToken.getToken());
        } else  {
            Log.e("NEW ACCESS TOKEN:", "YOU FUCKED UP NIGGA");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_activity, container, false);

    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.faceook_login_button);
        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setReadPermissions("email");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(mCallBackManager, mCallBack);

        if (savedInstanceState != null) {

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallBackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

}