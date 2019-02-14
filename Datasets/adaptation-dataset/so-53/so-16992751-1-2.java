public class foo {
@Override
public boolean onTouchEvent(MotionEvent ev){
    if(getCurrentItem()==getAdapter().getCount()-1){
        final int action = ev.getAction();
        float x = ev.getX();
        switch(action & MotionEventCompat.ACTION_MASK){
        case MotionEvent.ACTION_DOWN:
            mStartDragX = x;
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            if (x<mStartDragX){
                mListener.onSwipeOutAtEnd();
            }else{
                mStartDragX = 0;
            }
            break;
        }
    }else{
        mStartDragX=0;
    }
    return super.onTouchEvent(ev);
}
}