package com.papurrless;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;

/**
 * Created by Cam Geronimo on 11-1-2016.
 */
public class ImageHelper {

    private Context context;

    public ImageHelper(Context context){

        this.context = context;
    }

    public void saveImage(Bitmap image){
        try {
            File imageFile = null;

            String state = Environment.getExternalStorageState();
            File folder = null;
            if (state.contains(Environment.MEDIA_MOUNTED)) {
                folder = new File(Environment.getExternalStorageDirectory() + "/Papurrless");
            } else {
                folder = new File(Environment.getExternalStorageDirectory() + "/Papurrless");
            }
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdirs();
            }
            if (success) {
                java.util.Date date = new java.util.Date();
                imageFile = new File(folder.getAbsolutePath() + File.separator + new Timestamp(date.getTime()).toString() + "Image.JPEG");
                imageFile.createNewFile();

            } else {
                Toast.makeText(context, "Creating image phailed", Toast.LENGTH_LONG).show();

            }
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            //rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            FileOutputStream fout = new FileOutputStream(imageFile);
            fout.write(ostream.toByteArray());
            fout.close();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg");
            values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());

            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }




}
