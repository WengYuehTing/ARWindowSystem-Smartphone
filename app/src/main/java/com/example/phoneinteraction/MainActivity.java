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
        gestureHandler = new GestureHandler(windowWidth, windowHeight);
        view.setOnTouchListener(gestureHandler);

        YTNetwork.getInstance().createClient("192.168.1.208",60123,"smartphone-client");

    }

    // callback function when send string to server
    @Override
    public void onSendString(String message) {
        mTextView.setText(parser(message));
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

    private String parser(String message) {

        String[] array = message.split(GestureHandler.mediator);

        String res = "";

        switch (array[0]) {
            case "1":
                res += "单指";
                break;
            case "2":
                res += "双指";
                break;
            case "3":
                res += "三指";
                break;
            case "4":
                res += "四指";
                break;
            case "5":
                res += "五指";
                break;
        }

        switch (array[1]) {
            case "OneClick":
                res += "单击";
                break;
            case "DoubleClick":
                res += "双击";
                break;
            case "Up":
                res += "上滑";
                break;
            case "Down":
                res += "下滑";
                break;
            case "Left":
                res += "左滑";
                break;
            case "Right":
                res += "右滑";
                break;
            case "LPStart":
                res += "长按开始";
                break;
            case "LPEnd":
                res += "长按结束";
                break;
            case "LPUp":
                res += "长按上滑";
                break;
            case "LPDown":
                res += "长按下滑";
                break;
            case "LPLeft":
                res += "长按左滑";
                break;
            case "LPRight":
                res += "长按右滑";
                break;
        }

        return res;

    }
}
