package com.example.h.mogo;


import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;


public class MogoApp extends Application {
    public void onCreate() {
        super.onCreate();
        // Initialize Parse AND set local datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");

        // Initialize Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        //set parse facebook util for facebook user table
        ParseFacebookUtils.initialize(this);

    }

}
