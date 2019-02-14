public class foo {
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()){
        case MotionEvent.ACTION_DOWN: {
            Log.d("onTouch", "ACTION_DOWN");
            timeDown = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            return true;
        }
        case MotionEvent.ACTION_UP: {
            Log.d("onTouch", "ACTION_UP");
            long timeUp = System.currentTimeMillis();
            float upX = event.getX();
            float upY = event.getY();

            float deltaX = downX - upX;
            float absDeltaX = Math.abs(deltaX); 
            float deltaY = downY - upY;
            float absDeltaY = Math.abs(deltaY);

            long time = timeUp - timeDown;

            if (absDeltaY > MAX_OFF_PATH) {
                Log.i(logTag, String.format("absDeltaY=%.2f, MAX_OFF_PATH=%.2f", absDeltaY, MAX_OFF_PATH));
                return v.performClick();
            }

            final long M_SEC = 1000;
            if (absDeltaX > MIN_DISTANCE && absDeltaX > time * VELOCITY / M_SEC) {
                if(deltaX < 0) { this.onLeftToRightSwipe(v); return true; }
                if(deltaX > 0) { this.onRightToLeftSwipe(v); return true; }
            } else {
                Log.i(logTag, String.format("absDeltaX=%.2f, MIN_DISTANCE=%.2f, absDeltaX > MIN_DISTANCE=%b", absDeltaX, MIN_DISTANCE, (absDeltaX > MIN_DISTANCE)));
                Log.i(logTag, String.format("absDeltaX=%.2f, time=%d, VELOCITY=%d, time*VELOCITY/M_SEC=%d, absDeltaX > time * VELOCITY / M_SEC=%b", absDeltaX, time, VELOCITY, time * VELOCITY / M_SEC, (absDeltaX > time * VELOCITY / M_SEC)));
            }

        }
        }
        return false;
    }
}