package ru.ifmo.rain.korchagin.homework3_broadcastreceiver;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoaderService extends Service {

    private boolean isImageCached = false;
    private boolean isDownloadingNow = false;

    private static Integer DOWNLOADER_RESULT_SUCCESS = 1;
    private static Integer DOWNLOADER_RESULT_FAILURE = 2;
    private static String DOWNLOADER_TAG = "ImageDownloaderTag";
    static String DOWNLOADER_SUCCESS_BROADCAST = "ru.ifmo.rain.korchagin.homework3_broadcastreceiver.ImageDownloadedSuccessfully";

    void checkImage(String imagePath){
        File imageFile = new File(imagePath);
        isImageCached = imageFile.exists() && BitmapFactory.decodeFile(imagePath) != null;
    }

    class Downloader extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute(){
            isDownloadingNow = true;
        }

        @Override
        protected Integer doInBackground(String ... imagePath){
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            Log.d(DOWNLOADER_TAG, "Trying to download picture");
            try{
                URL imageURL = new URL(MainActivity.ImageHttpUrl);
                connection = (HttpURLConnection) imageURL.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(DOWNLOADER_TAG, "ResponseCode is not 200");
                    return DOWNLOADER_RESULT_FAILURE;
                }

                input = connection.getInputStream();
                output = new FileOutputStream(imagePath[0]);

                byte buffer[] = new byte[4096];
                int count;
                while((count = input.read(buffer)) > 0) {
                    output.write(buffer, 0, count);
                }
            } catch (Exception e1){
                return DOWNLOADER_RESULT_FAILURE;
            } finally {
                try{
                    if(input != null){
                        input.close();
                    }
                    if(output != null){
                        output.close();
                    }
                } catch (Exception e1){
                    // Do not do anything
                }
                if(connection != null){
                    connection.disconnect();
                }
            }
            Log.d(DOWNLOADER_TAG, "Downloaded successfully");
            return DOWNLOADER_RESULT_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result){
            if(result.equals(DOWNLOADER_RESULT_SUCCESS)){
                Intent intent = new Intent(DOWNLOADER_SUCCESS_BROADCAST);
                sendBroadcast(intent);
            }
            isDownloadingNow = false;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isDownloadingNow){
            return Service.START_NOT_STICKY;
        }
        String imagePath = getFilesDir().getAbsolutePath() + MainActivity.ImageName;
        checkImage(imagePath);
        if(!isImageCached && !isDownloadingNow){
            new Downloader().execute(imagePath);
        }
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
