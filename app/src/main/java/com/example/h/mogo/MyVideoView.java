package com.example.h.mogo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.VideoView;

public class MyVideoView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview_activity);

        final VideoView videoView =
                (VideoView) findViewById(R.id.video_view1);

        videoView.setVideoPath(
                //TODO: set the path from pares
                "http://www.ebookfrenzy.com/android_book/movie.mp4");

        videoView.start();
    }

}