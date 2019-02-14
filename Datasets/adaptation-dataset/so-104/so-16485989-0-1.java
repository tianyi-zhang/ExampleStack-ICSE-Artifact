public class foo {
@Override
public boolean onTouchEvent(MotionEvent ev) {
    switch (ev.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            mDownX = ev.getX();
            mDownY = ev.getY();
            isOnClick = true;
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            if (isOnClick) {
                Log.i(LOG_TAG, "onClick ");
                //TODO onClick code
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                Log.i(LOG_TAG, "movement detected");
                isOnClick = false;
            }
            break;
        default:
            break;
    }
    return true;
}
}