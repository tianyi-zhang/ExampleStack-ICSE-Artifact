public class foo{
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
}