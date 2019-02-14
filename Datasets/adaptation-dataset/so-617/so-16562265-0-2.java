public class foo {
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int eid = event.getAction();
        switch (eid)
        {
            case MotionEvent.ACTION_MOVE :
                PointF mv = new PointF( event.getX() - DownPT.x, event.getY() - DownPT.y);
                img.setX((int)(StartPT.x+mv.x));
                img.setY((int)(StartPT.y+mv.y));
                StartPT = new PointF( img.getX(), img.getY() );
                break;
            case MotionEvent.ACTION_DOWN :
                DownPT.x = event.getX();
                DownPT.y = event.getY();
                StartPT = new PointF( img.getX(), img.getY() );
                break;
            case MotionEvent.ACTION_UP :
                // Nothing have to do
                break;
            default :
                break;
        }
        return true;
    }
}