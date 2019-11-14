package com.example.phoneinteraction;

import androidx.appcompat.app.AppCompatActivity;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    View view;
    GestureHandler gestureHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager wm = this.getWindowManager();
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();

        view = findViewById(R.id.view);
        gestureHandler = new GestureHandler(this, windowWidth, windowHeight);
        view.setOnTouchListener(gestureHandler);

    }



}
