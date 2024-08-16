package com.akpdeveloper.baatcheet.utilities;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static java.lang.Math.abs;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context con){
        gestureDetector = new GestureDetector(con, new GestureListener());
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            onDownClick();
            return true;
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            onDoubleClick();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            onSingleClick();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(@NonNull MotionEvent e) {
            onLongClick();
            super.onLongPress(e);
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            try{
                int SWIPE_THRESHOLD = 100;
                int SWIPE_VELOCITY_THRESHOLD = 100;
                assert e1 != null;
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if(abs(diffX)>abs(diffY)){
                    if(abs(diffX)> SWIPE_THRESHOLD && abs(velocityX)> SWIPE_VELOCITY_THRESHOLD){
                        if(diffX>0){onSwipeRight();}else{onSwipeLeft();}
                    }
                }else{
                    if(abs(diffY)> SWIPE_THRESHOLD && abs(velocityY)> SWIPE_VELOCITY_THRESHOLD){
                        if(diffY<0){onSwipeUp();}else{onSwipeDown();}
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
    }
    public void onSwipeRight(){logcat("onSwipe Right");}
    public void onSwipeLeft(){logcat("onSwipeLeft");}
    public void onSwipeUp(){logcat("onSwipeUP");}
    public void onSwipeDown(){logcat("onSwipeDown");}
    public void onDoubleClick(){logcat("onDoubleClick");}
    public void onSingleClick(){logcat("onsingleClick");}
    public void onLongClick(){logcat("onLongClick");}
    public void onDownClick(){logcat("onDownclick");}
    public void onUpClick(){logcat("onupclick");}
}
