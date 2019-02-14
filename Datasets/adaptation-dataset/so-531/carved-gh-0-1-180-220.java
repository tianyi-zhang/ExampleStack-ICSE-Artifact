public class foo{
	/* See http://stackoverflow.com/questions/2373617/how-to-stop-scrolling-in-a-gallery-widget
	 * @see android.widget.Gallery#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		//Log.d("CustomGallery", "onFling called");
		userCreatedTouchEvent=true;
		//the animation event listener might not trigger
		ongoingAnimation=false;
		
		float velMax = 2500f;
	    float velMin = 1000f;
	    float velX = Math.abs(velocityX);
	    if (velX > velMax) {
	      velX = velMax;
	    } else if (velX < velMin) {
	      velX = velMin;
	    }
	    velX -= 600;
	    int k = 500000;
	    int speed = (int) Math.floor(1f / velX * k);
	    setAnimationDuration(speed);

	    int kEvent;
	    if (isScrollingLeft(e1, e2)) {
	      // Check if scrolling left
	      kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
	    } else {
	      // Otherwise scrolling right
	      kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
	    }
	    onKeyDown(kEvent, new FlingKeyEvent(0, 0));

	    return true;
		
	    //TODO:remove
		//nextSelection();
		//reducing the speed of the fling to a more suitable level
		//return super.onFling(e1, e2, velocityX/3, velocityY/3);
	}
}