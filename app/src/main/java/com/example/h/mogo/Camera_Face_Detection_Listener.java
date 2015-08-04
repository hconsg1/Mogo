package com.example.h.mogo;

import android.hardware.Camera;

/**
 * Created by USER on 2015-08-03.
 */
public class Camera_Face_Detection_Listener implements Camera.FaceDetectionListener{

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (faces.length > 0){
            //TODO: face is detected what next?
            System.out.println("\n\n\n\n\n================ FACE DETECTED ======================\n\n\n\n\n\n");
        }
    }
}
