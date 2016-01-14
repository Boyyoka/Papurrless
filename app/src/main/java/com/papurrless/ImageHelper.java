package com.papurrless;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

/**
 * Created by Cam Geronimo on 11-1-2016.
 */
public class ImageHelper {

    private Context context;

    public ImageHelper(Context context){

        this.context = context;
    }
    /**http://stackoverflow.com/questions/14535145/tesseract-recognition-fix*/
    public Bitmap processImage(Bitmap image){

        //Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

        return image;
    }


}
