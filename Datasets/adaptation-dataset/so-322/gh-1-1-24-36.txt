package com.qweex.nitrodroid;

//http://stackoverflow.com/questions/3554377/handling-click-events-on-a-drawable-within-an-edittext

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public abstract class RightDrawableOnTouchListener implements View.OnTouchListener {
    Drawable drawable;
    private int fuzz = 10;

    abstract boolean onDrawableTouch(final MotionEvent event);

    public RightDrawableOnTouchListener(TextView view) {
        super();
        final Drawable[] drawables = view.getCompoundDrawables();
        if (drawables != null && drawables.length == 4)
            this.drawable = drawables[2];
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && drawable != null) {
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            final Rect bounds = drawable.getBounds();
            if (x >= (v.getRight() - bounds.width() - fuzz) && x <= (v.getRight() - v.getPaddingRight() + fuzz)
                    && y >= (v.getPaddingTop() - fuzz) && y <= (v.getHeight() - v.getPaddingBottom()) + fuzz) {
                return onDrawableTouch(event);
            }
        }
        return false;
    }
}