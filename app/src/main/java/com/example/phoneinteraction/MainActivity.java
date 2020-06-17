package com.example.phoneinteraction;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import static android.Manifest.permission.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NetworkInterface, GestureCallback{

    View view;
    GestureHandler gestureHandler;

    TextView mTextView;
    CommandNetwork mCommandNetwork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.output);
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_LANDSCAPE);
        getSupportActionBar().hide();

        WindowManager wm = this.getWindowManager();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();

        CommandNetwork.getInstance().start();
//        YTNetwork.getInstance().createClient("192.168.1.104", 7777, "test");

        view = findViewById(R.id.view);
        gestureHandler = new GestureHandler(windowWidth, windowHeight);
        gestureHandler.setListener(this);
        view.setOnTouchListener(gestureHandler);

//        YTNetwork.getInstance().createClient("192.168.1.104",1219,"smartphone-client");

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MainActivity.this, Typing.class);
//                startActivity(intent);
//            }
//        }, 1000);
    }


    @Override
    public void onTriggered(Event event) {
        if(event.getType() != GestureType.Move) {
            String translated = event.translate();
            mTextView.setText(translated);
        }


        String textToSend = event.decode();
        CommandNetwork.getInstance().send(textToSend);
    }

    @Override
    public void onTracked(Event event, float x, float y) {
        String textToSend = event.decode();
        textToSend += "," + String.valueOf(x) + "," + String.valueOf(y) + ",";
        CommandNetwork.getInstance().send(textToSend);
    }

    // callback function when send string to server
    @Override
    public void onSendString(String message) {

    }


    // callback function when recv string from server
    @Override
    public void onReceiveString(String message) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        YTNetwork.getInstance().delegate = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        YTNetwork.getInstance().delegate = null;

    }
}
