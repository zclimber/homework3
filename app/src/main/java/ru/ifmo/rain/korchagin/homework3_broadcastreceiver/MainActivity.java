package ru.ifmo.rain.korchagin.homework3_broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    static String ImageName = "tux.jpg";
    static String ImageHttpUrl = "http://stuffpoint.com/cats/image/23672-cats-cute-cat.jpg";

    private BroadcastReceiver downloadCompleteReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String imagePath = getFilesDir().getAbsolutePath() + MainActivity.ImageName;
        File imageFile = new File(imagePath);
        if(imageFile.exists() && BitmapFactory.decodeFile(imagePath) != null){
            setImageVisible();
        } else {
            downloadCompleteReceiver = new DownloadCompleteReceiver();
            IntentFilter filter = new IntentFilter(ImageLoaderService.DOWNLOADER_SUCCESS_BROADCAST);
            registerReceiver(downloadCompleteReceiver, filter);
        }
    }

    void setImageVisible(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String imagePath = getFilesDir().getAbsolutePath() + MainActivity.ImageName;
        imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        imageView.setVisibility(View.VISIBLE);
        TextView errorText = (TextView) findViewById(R.id.textViewNoImage);
        errorText.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy(){
        if(downloadCompleteReceiver != null){
            unregisterReceiver(downloadCompleteReceiver);
        }
        super.onDestroy();
    }

    protected class DownloadCompleteReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            setImageVisible();
        }
    }
}
