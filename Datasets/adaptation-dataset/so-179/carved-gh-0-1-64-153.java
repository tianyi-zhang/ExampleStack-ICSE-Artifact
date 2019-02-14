public class foo{
	/**
	 * {@inheritDoc}
	 */
    public boolean onTouch(View v, MotionEvent event) 
    {
    	this.view = v;
    	
    	//swipe distance is scaled on a per-view basis (maybe?)
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
            if (listener != null)
        	{
        		listener.onStartSwipe(v);
        	}
            return true;
        case MotionEvent.ACTION_MOVE:
        case MotionEvent.ACTION_UP:
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;
            float deltaY = downY - upY;
            
            boolean returnVal = false;

            // swipe horizontal?
            if(Math.abs(deltaX) > minSwipeDistance)
            {
                // left or right
                if (deltaX < 0) 
                { 
                	if (listener != null)
                	{
                		listener.onRightSwipe(v);
                		returnVal = true;
                	}
                }
                else if (deltaX > 0) 
                { 
                	if (listener != null)
                	{
                		listener.onLeftSwipe(v);
                		returnVal = true;
                	}
                }
            }

            // swipe vertical?
            else if(Math.abs(deltaY) > minSwipeDistance)
            {
                // top or down
                if (deltaY < 0) 
                { 
                	if (listener != null)
                	{
                		listener.onDownSwipe(v);
                		returnVal = true;
                	} 
                }
                else if (deltaY > 0) 
                { 
                	if (listener != null)
                	{
                		listener.onUpSwipe(v);
                		returnVal = true;
                	} 
                }
            }
            if (event.getAction() == MotionEvent.ACTION_UP)
            {
            	if (listener != null)
            	{
            		listener.onStopSwipe(v);
            	}
            }
            return returnVal;
        }
        return false;
    }
}