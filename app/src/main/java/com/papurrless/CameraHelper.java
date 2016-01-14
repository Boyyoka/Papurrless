package com.papurrless;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.Toast;

import java.security.Policy;
import java.util.Collections;
import java.util.List;

/**
 * Created by Cam Geronimo on 9-1-2016.
 */

/** Please refer to the following links:
 *
 *  http://www.codeproject.com/Articles/840623/Android-Character-Recognition
 *  http://www.c-sharpcorner.com/UploadFile/9e8439/how-to-make-a-custom-camera-ion-android
 *
 */

public class CameraHelper{

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Context context;
    private WindowManager windowManager;
    private int rotation;
    boolean active;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {}
    };

    //Singleton
    private CameraHelper(SurfaceHolder surfaceHolder, Context context){
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        this.surfaceHolder = surfaceHolder;
        this.context = context;
    }

    public static CameraHelper New(SurfaceHolder surfaceHolder, Context context){

        return new CameraHelper(surfaceHolder, context);
    }

    public boolean isActive(){
        return active;
    }

    public void requestFocus(){
        if(camera == null){
            return;
        }
        if(isActive()){
            camera.autoFocus(autoFocusCallback);
        }
    }

    public void startCamera(int cameraId){

        try{
            camera = Camera.open(cameraId);
            if(camera != null){
                try{
                    initCamera(camera, cameraId);
                    camera.setPreviewDisplay(surfaceHolder);
                    camera.startPreview();
                    active = true;
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    stopCamera(cameraId);
                }
            }
        }
        catch(Exception ex){
            Toast.makeText(context,"Failed to start the camera", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    //http://stackoverflow.com/questions/16062225/custom-layout-with-windowmanager
    protected void initCamera(Camera camera, int cameraId){

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = windowManager.getDefaultDisplay().getRotation();

        int degree = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;
            default:
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
            rotation = (info.orientation - degree + 360) % 360;
            System.out.println("rotation: " + rotation);
        }

        camera.setDisplayOrientation(rotation);
        Parameters params = camera.getParameters();


        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes.contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }
        params.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public void stopCamera(int cameraId){
        if(camera != null){
            camera.release();
            camera = null;
        }
        active = false;
    }

    public void captureImage(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawPictureCallback, Camera.PictureCallback jpegPictureCallback){
        if(isActive()){
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }

    public int getRotation(){
        return rotation;
    }
}
