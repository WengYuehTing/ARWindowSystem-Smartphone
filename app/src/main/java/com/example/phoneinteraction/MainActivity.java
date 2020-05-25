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

public class MainActivity extends AppCompatActivity implements NetworkInterface{

    View view;
    GestureHandler gestureHandler;

    TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.output);

        WindowManager wm = this.getWindowManager();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();

        getSupportActionBar().hide();

        view = findViewById(R.id.view);
        gestureHandler = new GestureHandler(mTextView, windowWidth, windowHeight);
        view.setOnTouchListener(gestureHandler);

        YTNetwork.getInstance().createClient("192.168.1.104",1219,"smartphone-client");

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(MainActivity.this, Typing.class);
//                startActivity(intent);
//            }
//        }, 1000);
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
