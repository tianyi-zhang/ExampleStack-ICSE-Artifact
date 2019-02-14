public class foo {
  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
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
    onKeyDown(kEvent, null);

    return true;
  }
}