package com.example.h.mogo;

/**
 * Created by USER on 2015-08-05.
 */
import com.parse.Parse;
import android.app.Application;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;


public class MogoApp extends Application {
    public void onCreate() {

        // Initialize Parse AND set local datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "Twc5KQkxtq0yenh2uHBp3GVwfqW48kwKIgLThZvM", "vLlrTC1DrowiyZJtzURRuSUpI64dOFvoBt1AqRIC");

        // Initialize Facebook
        String appId = getString(R.string.facebook_app_id);

        //set parse facebook util for facebook user table
        ParseFacebookUtils.initialize(context, callbackRequestCodeOffset);
        ParseFacebookUtils.initialize(appId);

    }

}
