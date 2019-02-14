public class foo{
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
}