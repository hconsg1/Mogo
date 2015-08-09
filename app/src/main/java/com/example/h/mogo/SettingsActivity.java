package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SettingsActivity extends Activity
{


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //Button   Back to Main Feed
        ImageButton closebutton = (ImageButton) findViewById(R.id.settings_button_close);
        closebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent close_settings_intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(close_settings_intent);
                overridePendingTransition(R.anim.animation_push_left_in, R.anim.animation_push_left_out);
            }
        });
    }
}
