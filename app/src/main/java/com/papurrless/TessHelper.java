package com.papurrless;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Cam Geronimo on 10-1-2016.
 */
public class TessHelper {
    private static final String lang = "eng";
    private File packageFolder;


    public TessHelper(Context context){
        createFolder(context);
    }

    private void createFolder(Context context) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        packageFolder = new File(extStorageDirectory, "/Android/data/" + context.getPackageName() + "/tessdata/");
        if(!packageFolder.exists())
        {
            packageFolder.mkdirs();
            createAssets(context);
            Toast.makeText(context, "Folder Created At :" + packageFolder.getPath().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void createAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String files[] = null;
        try {
            files = assetManager.list("tessdata");

           // OutputStream out = new FileOutputStream(tessDataFolder.getPath().toString() + "/tessdata/" + lang + ".traineddata");

            }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        for(String filename : files){
            InputStream in = null;
            OutputStream out = null;
            try{

                in = assetManager.open("tessdata/"+filename);
                String path = packageFolder.getPath();
                File fileOutput = new File(path, filename);

                out = new FileOutputStream(fileOutput);
                copyTessFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void copyTessFile(InputStream in, OutputStream out)throws IOException{
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public String detectText(Bitmap bitmap){
        //TessDataManager.initTessTrainedData();
        TessBaseAPI tessBaseAPI = new TessBaseAPI();

        String path = "/mnt/sdcard/Android/data/com.papurrless";
        tessBaseAPI.setDebug(true);
        tessBaseAPI.init(path, "eng");

        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890qwertyuiopYTREWQasdASDfghFGHjklJKLlxcvXCVbnmBNMPOIU");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{" +
          ";:'\"\\|~`,./<>?");

        tessBaseAPI.setImage(bitmap);
        String text = tessBaseAPI.getUTF8Text();

        //System.out.println(text);


        tessBaseAPI.end();
        return text;
    }

}

