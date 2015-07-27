package com.example.h.mogo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.VideoView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseFile;

public class MyVideoView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videoview_activity);
        this.getVideofromParse();



    }

    public void getVideofromParse(){
        System.out.println("=========== get videp from parser astasr========================");
        ParseQuery<ParseObject> query = ParseQuery.getQuery("VideoUpload");
        ParseObject object = null;
        String myurl=null;
        try{
            object = query.get("MXYTbgBWc3");
            ParseFile applicantResume = (ParseFile)object.get("firstUpload");
            System.out.println("++++++++++++"+applicantResume);
            try {
                myurl = applicantResume.getUrl();
            }catch(Exception e){
                System.out.println("parseException");
                System.out.println("===============================ERROR =======================");
                e.printStackTrace();
            }
            this.turn_on_video(myurl);
            System.out.println("ggggggggggggggggggg"+ myurl + "$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("=========== 6666666666666666666666666666666666666   =======================");
            System.out.print("/////////////////////////////////////////!!!!!!"+ myurl);


        } catch( ParseException e){
            System.out.println("parseException");
        }

    }

    public void turn_on_video(String url){
        final VideoView myvideoview = (VideoView)findViewById(R.id.video_view1);
        myvideoview.setVideoPath(url);
        myvideoview.start();
    }

}