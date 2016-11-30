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

    public static final String IMAGE_NAME = "cat.jpg";
    public static final String IMAGE_HTTP_URL = "http://stuffpoint.com/cats/image/23672-cats-cute-cat.jpg";
    public static final String DOWNLOADER_SUCCESS_BROADCAST = "ru.ifmo.rain.korchagin.homework3_broadcastreceiver.IMAGE_DOWNLOADED";

    private BroadcastReceiver downloadCompleteReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String imagePath = getFilesDir().getAbsolutePath() + MainActivity.IMAGE_NAME;
        File imageFile = new File(imagePath);
        if(imageFile.exists() && BitmapFactory.decodeFile(imagePath) != null){
            setImageVisible();
        } else {
            downloadCompleteReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    setImageVisible();
                }
            };
            IntentFilter filter = new IntentFilter(DOWNLOADER_SUCCESS_BROADCAST);
            registerReceiver(downloadCompleteReceiver, filter);
        }
    }

    void setImageVisible(){
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        String imagePath = getFilesDir().getAbsolutePath() + MainActivity.IMAGE_NAME;
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
}
