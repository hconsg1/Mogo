package com.example.h.mogo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends Activity
{

    private String grid_info;
    private ArrayList<Object> list = new ArrayList<Object>();

    /** Declaring an ArrayAdapter to set items to ListView */
    private ArrayAdapter<Object> adapter;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);
        grid_info = this.getIntent().getExtras().getString("grid_info");
        //Button   Back to Main Feed
        ImageButton closebutton = (ImageButton) findViewById(id.notification_button_close);
        closebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent close_notification_intent = new Intent(NotificationActivity.this, MainActivity.class);
                startActivity(close_notification_intent);
                overridePendingTransition(R.anim.animation_push_right_in, R.anim.animation_push_right_out);
            }
        });

        //Button   Open Settings
        ImageButton settingsbutton = (ImageButton) findViewById(id.notification_button_setting);
        settingsbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent open_settings_intent = new Intent(NotificationActivity.this, SettingsActivity.class);
                startActivity(open_settings_intent);
                overridePendingTransition(R.anim.animation_push_left_in, R.anim.animation_push_left_out);
            }
        });

        get_notification_feed(grid_info);
    }//end of oncreate

    public void get_notification_feed(String grid_info){
        final ListView list_view = (ListView) findViewById(R.id.notification_wrapper);
        list_view.removeAllViews();

        //TODO: get all the video with the same grid index from parse and set the url of the videos to each of them
        List<ParseObject> objectList;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("request_table");
        query.whereEqualTo("grid_index", grid_info);
        query.orderByAscending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> list, com.parse.ParseException e) {
                if (e == null) {
                    //TODO: SUCCESS
                    adapter = new ArrayAdapter<String>(this, R.layout.notification_list_item, list);
                    //TODO: loop through query returned NOTIFICATION objects
                    List<Object> myList = new ArrayList<Object>();
                    for (ParseObject object : list) {
                        String[] data_item = new String[3];
                        data_item[0] = object.getString("grid_index");
                        data_item[1] = object.getString("requested_user");
                        String lat = Double.toString(object.get("location").getLatitude());
                        String lon = Double.toString(object.get("location").getLongitude());
                        data_item[2] = lat + "/" + lon;

                        myList.add(data_item);
                        LinearLayout linearLayout = new LinearLayout(NotificationActivity.this);
                        TextView textView = new TextView(NotificationActivity.this);
                        textView.setText("MESSAGE");

                        linearLayout.addView(textView);
                        list_view.addView(linearLayout);

                        linearLayout.setTag(data_item);




                    }


                } else {
                    //TODO :ERROR

                }
            }


    }
}
