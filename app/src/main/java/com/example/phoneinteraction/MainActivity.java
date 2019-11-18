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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

        view = findViewById(R.id.view);
        gestureHandler = new GestureHandler(mTextView, windowWidth, windowHeight);
        view.setOnTouchListener(gestureHandler);

        YTNetwork.getInstance().createClient("192.168.1.208",60123,"smartphone-client");

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
