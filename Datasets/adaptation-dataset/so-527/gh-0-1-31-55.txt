package me.hyunbin.transit;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Hyunbin on 7/1/15.
 * Fixes out of bound error found on certain devices spontaneously.
 *
 * Code based on solution from:
 * http://stackoverflow.com/questions/27662682/illegalargumentexception-pointerindex-out-of-range-from-swiperefreshlayout
 */

public class BetterSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mActivePointerId;

    public BetterSwipeRefreshLayout(Context context) {
        super(context);
    }

    public BetterSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_CANCEL) {
            int pointerCount = MotionEventCompat.getPointerCount(ev);
            int index = MotionEventCompat.getActionIndex(ev);
            mActivePointerId = MotionEventCompat.getPointerId(ev, index);
            index = MotionEventCompat.findPointerIndex(ev,mActivePointerId);
            if (index > -1 && index < pointerCount) {
                super.onInterceptTouchEvent(ev);
            } else {
                return true;
            }
        }else if(ev.getAction() == MotionEventCompat.ACTION_POINTER_DOWN && super.onInterceptTouchEvent(ev)) {
            final int index = MotionEventCompat.getActionIndex(ev);
            mActivePointerId = MotionEventCompat.getPointerId(ev, index);
            return false;
        }else if(ev.getAction() == MotionEventCompat.ACTION_POINTER_UP && super.onInterceptTouchEvent(ev)){
            onSecondaryPointerUp(ev);
            return false;
        }else if(ev.getAction() == MotionEvent.ACTION_DOWN && super.onInterceptTouchEvent(ev)){
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_CANCEL) {
            int pointerCount = MotionEventCompat.getPointerCount(ev);
            int index = MotionEventCompat.getActionIndex(ev);
            mActivePointerId = MotionEventCompat.getPointerId(ev, index);
            index = MotionEventCompat.findPointerIndex(ev,mActivePointerId);
            if (index > -1 && index < pointerCount) {
                try{
                    super.onTouchEvent(ev);
                } catch (Exception e){
                    // Seems to bug out only when activity is about to be destroyed,
                    // so catch without any error handling for now.
                    Crashlytics.logException(e);
                    return true;
                }
            } else {
                return true;
            }
        }else if(ev.getAction() == MotionEventCompat.ACTION_POINTER_DOWN && super.onTouchEvent(ev)) {
            final int index = MotionEventCompat.getActionIndex(ev);
            mActivePointerId = MotionEventCompat.getPointerId(ev, index);
            return false;
        }else if(ev.getAction() == MotionEventCompat.ACTION_POINTER_UP && super.onTouchEvent(ev)){
            onSecondaryPointerUp(ev);
            return false;
        }else if(ev.getAction() == MotionEvent.ACTION_DOWN && super.onTouchEvent(ev)){
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            return false;
        }
        return super.onTouchEvent(ev);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }
}
