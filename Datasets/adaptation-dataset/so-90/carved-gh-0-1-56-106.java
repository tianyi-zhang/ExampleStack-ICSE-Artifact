public class foo{
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN:
            MyLog.d("onTouch", "ACTION_DOWN");
            timeDown = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            return false;
        case MotionEvent.ACTION_UP: 
            MyLog.d("onTouch", "ACTION_UP");
            long timeUp = System.currentTimeMillis();
            float upX = event.getX();
            float upY = event.getY();

            float deltaX = downX - upX;
            float absDeltaX = Math.abs(deltaX); 
            float deltaY = downY - upY;
            float absDeltaY = Math.abs(deltaY);

            long time = timeUp - timeDown;

            if (absDeltaY > maxOffPath) {
                MyLog.v(this, String.format("absDeltaY=%.2f, MAX_OFF_PATH=%.2f", absDeltaY, maxOffPath));
                return v.performClick();
            }

            final long milliSec = 1000;
            if (absDeltaX > minDistance && absDeltaX > time * velocity / (float) milliSec) {
                if(deltaX < 0) { 
                    this.onLeftToRightSwipe(v); 
                    return true; 
                }
                if(deltaX > 0) { 
                    this.onRightToLeftSwipe(v); 
                    return true; 
                }
            } else {
                MyLog.v(this, String.format("absDeltaX=%.2f, MIN_DISTANCE=%.2f, absDeltaX > MIN_DISTANCE=%b", 
                        absDeltaX, minDistance, 
                        absDeltaX > minDistance));
                MyLog.v(this, String.format("absDeltaX=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaX > time * VELOCITY / M_SEC=%b", 
                        absDeltaX, time, velocity, time * velocity / milliSec, 
                        absDeltaX > time * velocity / (float) milliSec));
            }
            break;
        default:
            break;
        }
        return false;
    }
}