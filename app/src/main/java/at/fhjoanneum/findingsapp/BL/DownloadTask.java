package at.fhjoanneum.findingsapp.BL;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import at.fhjoanneum.findingsapp.R;

public class DownloadTask extends AsyncTask<Bitmap, Void, Boolean> {
    private DownloadCallback callback;
    private Context context;

    public DownloadTask(Context context, DownloadCallback callback) {
        this.callback = callback;
        this.context = context;
    }
    //executed on UI thread
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (callback != null) {
            callback.onDownloadStart();
        }
    }

    //executed on background thread
    @Override
    protected Boolean doInBackground(Bitmap... bitmaps) {
        try{
            saveImage(bitmaps[0],context,context.getResources().getString(R.string.app_name));
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    //executed on UI thread
    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (callback != null) {
            callback.onResult(result);
        }
    }
    private void saveImage(Bitmap bitmap, Context context, String folderName){
        if (Build.VERSION.SDK_INT >= 29) {
            ContentValues values = getContentValues();
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
            values.put(MediaStore.Images.Media.IS_PENDING, true);
            Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                context.getContentResolver().update(uri, values, null, null);
            }
        } else {
            File directory = new File(Environment.getExternalStorageDirectory().toString() + File.separator+ folderName);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            String fileName = System.currentTimeMillis() + ".png";
            File file = new File(directory, fileName);
            try {
                saveImageToStream(bitmap, new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.getAbsolutePath() != null) {
                ContentValues  values = getContentValues();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }
    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }
    private void saveImageToStream(Bitmap bitmap, OutputStream outputStream) throws IOException {
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
        }
    }
}
