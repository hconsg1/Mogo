package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class NotificationActivity extends Activity
{


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        //Button   Back to Main Feed
        ImageButton closebutton = (ImageButton) findViewById(R.id.notification_button_close);
        closebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent close_notification_intent = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(close_notification_intent);
                overridePendingTransition(R.anim.animation_push_right_in, R.anim.animation_push_right_out);
            }
        });

        //Button   Open Settings
        ImageButton settingsbutton = (ImageButton) findViewById(R.id.notification_button_setting);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent open_settings_intent = new Intent(NotificationActivity.this, SettingsActivity.class);
                startActivity(open_settings_intent);
                overridePendingTransition(R.anim.animation_push_left_in, R.anim.animation_push_left_out);
            }
        });
    }
}
