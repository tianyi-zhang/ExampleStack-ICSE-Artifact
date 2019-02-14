public class foo {
    /**
     * {@inheritDoc}
     */
    public boolean onTouch(View v, MotionEvent event)
    {
        if (config == null)
        {
                config = ViewConfiguration.get(v.getContext());
                minSwipeDistance = config.getScaledTouchSlop();
        }

        switch(event.getAction())
        {
        case MotionEvent.ACTION_DOWN:
            downX = event.getX();
            downY = event.getY();
            return true;
        case MotionEvent.ACTION_UP:
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;
            float deltaY = downY - upY;

            // swipe horizontal?
            if(Math.abs(deltaX) > minSwipeDistance)
            {
                // left or right
                if (deltaX < 0)
                {
                        if (listener != null)
                        {
                                listener.onRightSwipe(v);
                                return true;
                        }
                }
                if (deltaX > 0)
                {
                        if (listener != null)
                        {
                                listener.onLeftSwipe(v);
                                return true;
                        }
                }
            }

            // swipe vertical?
            if(Math.abs(deltaY) > minSwipeDistance)
            {
                // top or down
                if (deltaY < 0)
                {
                        if (listener != null)
                        {
                                listener.onDownSwipe(v);
                                return true;
                        }
                }
                if (deltaY > 0)
                {
                        if (listener != null)
                        {
                                listener.onUpSwipe(v);
                                return true;
                        }
                }
            }
        }
        return false;
    }
}