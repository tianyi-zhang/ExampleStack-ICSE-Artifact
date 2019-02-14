public class foo{
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (getCurrentItem() == 0 || getCurrentItem() == getAdapter().getCount() - 1) {
            final int action = ev.getAction();
            float x = ev.getX();
            switch (action & MotionEventCompat.ACTION_MASK) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (getCurrentItem() == 0 && x > mStartDragX) {
                        mListener.onSwipeOutAtStart();
                    }
                    if (getCurrentItem() == getAdapter().getCount() - 1 && x < mStartDragX) {
                        mListener.onSwipeOutAtEnd();
                    }
                    break;
            }
        } else {
            mStartDragX = 0;
        }
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }
}