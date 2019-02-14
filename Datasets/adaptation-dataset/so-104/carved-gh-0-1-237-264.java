public class foo{
            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = ev.getX();
                        mDownY = ev.getY();
                        isOnClick = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (isOnClick) {
//                            Log.i(TAG, "onClick ");
                            showToolbar();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
//                            Log.i(TAG, "movement detected");
                            isOnClick = false;
                        }
                        break;
                    default:
                        break;
                }

                return false;
            }
}