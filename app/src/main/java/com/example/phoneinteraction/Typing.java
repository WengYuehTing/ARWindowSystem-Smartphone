package com.example.phoneinteraction;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Typing extends AppCompatActivity {

    public ImageView mImageView;
    public int[] drawables = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.night, R.drawable.ten, R.drawable.eleven, R.drawable.twelve, R.drawable.thirteen, R.drawable.fourteen, R.drawable.fifteen, R.drawable.sixteen, R.drawable.seventeen, R.drawable.eighteen, R.drawable.nighteen};
    public int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typing);

        getSupportActionBar().hide();

        mImageView = findViewById(R.id.imageView);

        mImageView.setOnTouchListener(new ImageView.OnTouchListener(){

            @Override
            public boolean onTouch(View arg0, final MotionEvent motionEvent) {
                runOnUiThread(new Runnable() {
                    final MotionEvent me = motionEvent;
                    @Override
                    public void run() {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                            Drawable drawable = getResources().getDrawable(drawables[index++ % drawables.length]);
                            mImageView.setImageDrawable(drawable);

                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Drawable drawable2 = getResources().getDrawable(drawables[index++ % drawables.length]);
                                    mImageView.setImageDrawable(drawable2);
                                }
                            }, 100);

                        }
                    }
                });

                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {  //起來的時候恢復背景與顏色
                    Log.d("Piggy","Release");
                }
                return true;
            }
        });

    }
}
