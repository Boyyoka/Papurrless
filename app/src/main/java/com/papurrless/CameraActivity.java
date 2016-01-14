package com.papurrless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/** Please refer to the following links:
 *
 *  http://www.codeproject.com/Articles/840623/Android-Character-Recognition
 *  http://www.c-sharpcorner.com/UploadFile/9e8439/how-to-make-a-custom-camera-ion-android
 *
 */

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, OnClickListener, Camera.PictureCallback, Camera.ShutterCallback {

    private CameraHelper cameraHelper;
    private Button shutterButton;
    private SurfaceView cameraFrame;
    private ImageHelper imageHelper;
    private TessHelper tessHelper;
    private int cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        shutterButton = (Button) findViewById(R.id.snap);
        cameraId=Camera.CameraInfo.CAMERA_FACING_BACK;
        cameraFrame=(SurfaceView) findViewById(R.id.surfaceView);
        shutterButton.setOnClickListener(this);
        imageHelper = new ImageHelper(this);
        tessHelper = new TessHelper(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }
    /**
     *  http://stackoverflow.com/questions/14678593/the-application-may-be-doing-too-much-work-on-its-main-thread
     *  http://stackoverflow.com/questions/22271685/issue-with-the-android-camera-and-thread-safety
     *
     * */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        final SurfaceHolder h = holder;
        Thread cameraThread = new Thread() {
            @Override
            public void run() {
                if (cameraHelper != null && !cameraHelper.isActive())
                {
                    cameraHelper.startCamera(cameraId);
                } else if (cameraHelper != null && cameraHelper.isActive())

                {
                    //Camera is already activated
                    return;
                } else

                {
                    cameraHelper = CameraHelper.New(h, CameraActivity.this);
                    cameraHelper.startCamera(cameraId);
                }
            }
        };
        cameraThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onResume(){
        super.onResume();

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void onPause(){
        super.onPause();

        if(cameraHelper != null && cameraHelper.isActive()){
            cameraHelper.stopCamera(cameraId);
        }
        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {
        if(v == shutterButton){

            if(cameraHelper != null && cameraHelper.isActive()){

                cameraHelper.captureImage(this, this, this);
            }
        }
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        if(data == null){
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        Bitmap capturedImage = BitmapFactory.decodeByteArray(data, 0, data.length);

        try{

            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(cameraHelper.getRotation());

            Bitmap rotatedBitmap = Bitmap.createBitmap(capturedImage, 0, 0, capturedImage.getWidth(), capturedImage.getHeight(), rotateMatrix, false);
            rotatedBitmap = Bitmap.createScaledBitmap(rotatedBitmap, capturedImage.getWidth() * 2, capturedImage.getHeight() * 2, false);
            //rotatedBitmap = imageHelper.processImage(rotatedBitmap);
            rotatedBitmap = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Toast.makeText(this,tessHelper.detectText(rotatedBitmap), Toast.LENGTH_LONG).show();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }




        //imageHelper.processImage(tessImage);

    }

    @Override
    public void onShutter() {

    }

}
