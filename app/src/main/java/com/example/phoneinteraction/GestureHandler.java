package com.example.phoneinteraction;

import android.app.Activity;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


enum GestureType {

    OneClick,
    DoubleClick,
    Up,
    Down,
    Left,
    Right,
    LPStart,
    LPEnd,
    LPUp,
    LPDown,
    LPLeft,
    LPRight
};

public class GestureHandler implements View.OnTouchListener {

    // 长按时间界限（超过此时间则为长按）
    private static final long LONGPRESS_TIME_THRESHOLD = 300;

    // 双击事件界限（两次点击间隔在此时间以内则为双击）
    private static final long DOUBLECLICK_TIME_THRESHOLD = 300;

    // 长按手抖容忍界限（距离超过此距离则不为长按）
    private static final float LONGPRESS_SHAKE_LIMIT = 40.0f;

    // 点击手抖容忍界限（距离超过此距离则不为长按）
    private static final float PRESS_SHAKE_LIMIT = 100.0f;

    private static final int MULTI_FINGER_YOFFSET = 253;

    // 上下滑距离界限（超过此距离则为合法滑动）
    private static final float GESTURE_VERTICAL_THREOLD = 200.0f;
    private static final float GESTURE_LONG_VERTICAL_THREOLD = 600.0f;

    // 左右滑距离界限（超过此距离则为合法滑动）
    private static final float GESTURE_HORIZONTAL_THREOLD = 200.0f;

    // 判断上滑下滑时对左右移动的容忍界限
    private static final float GESTURE_VERTICAL_LIMIT = 200.0f;

    // 判断左滑右滑时对上下移动的容忍界限
    private static final float GESTURE_HORIZONTAL_LIMIT = 200.0f;

    private static final float GESTURE_BORDER_WIDTH = 150.0f;

    public static final String mediator = "-";

    private Handler handler = new Handler();

    private class Point {
        public float x;
        public float y;

        public Point() {}

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        float distance(Point p) {
            return (float)Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
        }

        float verticalDistance(Point p) {
            return Math.abs(p.y - y);
        }

        float horizontalDistance(Point p) {
            return Math.abs(p.x - x);
        }

        boolean leftOf(Point p) {
            return x < p.x;
        }

        boolean rightOf(Point p) {
            return x > p.x;
        }

        boolean above(Point p) {
            return y < p.y;
        }

        boolean under(Point p) {
            return y > p.y;
        }

        void print() {

            Log.d("YueTing", x + " " + y);
        }
    }

    private Point startPoint = new Point();
    private Point endPoint = new Point();


    private class LongPressRunnable implements Runnable {
        public boolean pressing = false;
        public boolean added = false;
        public int fingers;

        @Override
        public void run() {
            pressing = true;
            String command = String.valueOf(fingers) + mediator + GestureType.LPStart.toString();
            YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
        }

        public void end() {

            String command = String.valueOf(fingers) + mediator + GestureType.LPEnd.toString();
            YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
        }
    }

    private LongPressRunnable longPressRunnable = new LongPressRunnable();

    private class SingleClickRunnable implements Runnable {
        public boolean finished;
        public int fingers;

        @Override
        public void run() {

            String command = String.valueOf(fingers) + mediator + GestureType.OneClick.toString();
            YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);

            finished = true;
        }
    }

    private SingleClickRunnable singleClickRunnable = new SingleClickRunnable();

    private class FlipGestureRunnable implements Runnable {
        public Point startPoint;
        public Point endPoint;
        public int fingers;

        @Override
        public void run() {

            // Implement flip gesture method here
            if(endPoint.verticalDistance(startPoint) >= GESTURE_VERTICAL_THREOLD && endPoint.horizontalDistance(startPoint) < GESTURE_HORIZONTAL_LIMIT) {
                if (endPoint.above(startPoint)) {
                    // 上滑
                    String command = String.valueOf(fingers) + mediator + GestureType.Up.toString();
                    YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
                }

                else if(endPoint.under(startPoint)) {
                    // 下滑
                    String command = String.valueOf(fingers) + mediator + GestureType.Down.toString();
                    YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
                }
            }

            else if(endPoint.horizontalDistance(startPoint) >= GESTURE_HORIZONTAL_THREOLD && endPoint.verticalDistance(startPoint) < GESTURE_VERTICAL_LIMIT) {
                if(endPoint.leftOf(startPoint)) {
                    // 左滑
                    String command = String.valueOf(fingers) + mediator + GestureType.Left.toString();
                    YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
                }

                else if(endPoint.rightOf(startPoint)) {
                    // 右滑
                    String command = String.valueOf(fingers) + mediator + GestureType.Right.toString();
                    YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);
                }
            }
        }
    }

    private FlipGestureRunnable flipGestureRunnable = new FlipGestureRunnable();


    private class DoubleClickRunnable implements Runnable {

        public int fingers;
        @Override
        public void run() {

            // Implement double click method here
            String command = String.valueOf(fingers) + mediator + GestureType.DoubleClick.toString();
            YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);

        }
    }

    private DoubleClickRunnable doubleClickRunnable = new DoubleClickRunnable();

    private int windowWidth;

    private int windowHeight;

    
    private static String TAG = "Smartphone-Interactions";

    public GestureHandler(int windowWidth, int windowHeight) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        singleClickRunnable.finished = true;
    }


    private int fingers = 1;
    private boolean handled = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                startPoint.x = event.getRawX();
                startPoint.y = event.getRawY();
                addLongPressCallback();

                //reset
                handled = false;
                fingers = 1;
                singleClickRunnable.fingers = 1;
                doubleClickRunnable.fingers = 1;
                flipGestureRunnable.fingers = 1;
                longPressRunnable.fingers = 1;

                startPoint.print();

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = event.getPointerCount();
                longPressRunnable.fingers = fingers;
                break;

            case MotionEvent.ACTION_MOVE:
                endPoint.x = event.getRawX();
                endPoint.y = event.getRawY();
                if (!longPressRunnable.pressing) {
                    if (startPoint.distance(endPoint) > LONGPRESS_SHAKE_LIMIT) {
                        removeLongPressCallBack();
                    }
                }

                break;

            case MotionEvent.ACTION_POINTER_UP:

                Log.d("YueTing","Action Pointer Up");

                if(handled) { break; }

                endPoint.x = event.getX(0);
                endPoint.y = event.getY(0) + MULTI_FINGER_YOFFSET;
                endPoint.print();

                if (longPressRunnable.pressing) {
                    handleLongGesture(startPoint,endPoint,fingers);
                    removeLongPressCallBack();
                }

                else if(endPoint.distance(startPoint) >= PRESS_SHAKE_LIMIT) {
                    flipGestureRunnable.startPoint = startPoint;
                    flipGestureRunnable.endPoint = endPoint;
                    flipGestureRunnable.fingers = fingers;
                    flipGestureRunnable.run();
                }

                else if (singleClickRunnable.finished){
                    removeLongPressCallBack();
                    singleClickRunnable.fingers = fingers;
                    addSingleClickCallback();
                }

                else {
                    removeSingleClickCallback();
                    removeLongPressCallBack();
                    doubleClickRunnable.fingers = fingers;
                    doubleClickRunnable.run();
                }

                handled = true;

                break;

            case MotionEvent.ACTION_UP:

                Log.d("YueTing","Action Up");

                endPoint.x = event.getRawX();
                endPoint.y = event.getRawY();

                if(fingers == 1 && !handled){
                    if (longPressRunnable.pressing) {
                        handleLongGesture(startPoint,endPoint,fingers);
                        removeLongPressCallBack();
                    }

                    else if(endPoint.distance(startPoint) >= PRESS_SHAKE_LIMIT) {
                        flipGestureRunnable.startPoint = startPoint;
                        flipGestureRunnable.endPoint = endPoint;
                        flipGestureRunnable.fingers = fingers;
                        flipGestureRunnable.run();
                    }

                    else if (singleClickRunnable.finished){
                        removeLongPressCallBack();
                        singleClickRunnable.fingers = fingers;
                        addSingleClickCallback();
                    }
                    else {
                        removeSingleClickCallback();
                        removeLongPressCallBack();
                        doubleClickRunnable.run();
                    }
                } else {
                    removeLongPressCallBack();
                }

                break;

            default:
                break;
        }

        return true;
    }

    private void addSingleClickCallback() {

        singleClickRunnable.finished = false;
        handler.postDelayed(singleClickRunnable, DOUBLECLICK_TIME_THRESHOLD);
    }

    private void removeSingleClickCallback() {
        handler.removeCallbacks(singleClickRunnable);
        singleClickRunnable.finished = true;
    }

    private void addLongPressCallback() {
        longPressRunnable.pressing = false;
        handler.postDelayed(longPressRunnable, LONGPRESS_TIME_THRESHOLD);
        longPressRunnable.added = true;

    }

    private void removeLongPressCallBack() {
        handler.removeCallbacks(longPressRunnable);
        longPressRunnable.added = false;
        longPressRunnable.pressing = false;
    }

    private int onBorder(Point p) {

        if(p.x < GESTURE_BORDER_WIDTH) {
            return 2;
        } else if(p.x > windowWidth - GESTURE_BORDER_WIDTH) {
            return 1;
        } else {
            return 0;
        }
    }

    private void handleLongGesture(Point startPoint, Point endPoint, int fingers) {

        // 标记是否在边缘
        int border = onBorder(startPoint);

        
        // 判断上下滑
        if (startPoint.verticalDistance(endPoint) > GESTURE_LONG_VERTICAL_THREOLD &&
                startPoint.horizontalDistance(endPoint) < GESTURE_VERTICAL_LIMIT) {
            if (endPoint.above(startPoint)) {

                String command = String.valueOf(fingers) + mediator + GestureType.LPUp.toString();
                YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);

            }
            else if (endPoint.under(startPoint)) {

                String command = String.valueOf(fingers) + mediator + GestureType.LPDown.toString();
                YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);

            }
        }

        else if (startPoint.horizontalDistance(endPoint) > GESTURE_HORIZONTAL_THREOLD &&
                startPoint.verticalDistance(endPoint) < GESTURE_HORIZONTAL_LIMIT) {
            if (endPoint.leftOf(startPoint)) {

                String command = String.valueOf(fingers) + mediator + GestureType.LPLeft.toString();
                YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);


            }
            else if (endPoint.rightOf(startPoint)) {

                String command = String.valueOf(fingers) + mediator + GestureType.LPRight.toString();
                YTNetwork.getInstance().getFirstClient().getSocket().sendString(command);

            }
        }

        longPressRunnable.end();
    }


}
