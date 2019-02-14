public class foo{
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (!isEnabled()) {
      return false;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        setSelected(true);
        setPressed(true);
        if (changeListener != null)
          changeListener.onStartTrackingTouch(this);
        break;
      case MotionEvent.ACTION_UP:
        setSelected(false);
        setPressed(false);
        if (changeListener != null)
          changeListener.onStopTrackingTouch(this);
        break;
      case MotionEvent.ACTION_MOVE:
        int progress=
            getMax() - (int)(getMax() * event.getY() / getHeight());
        setProgress(progress);
        onSizeChanged(getWidth(), getHeight(), 0, 0);
        if (changeListener != null)
          changeListener.onProgressChanged(this, progress, true);
        break;

      case MotionEvent.ACTION_CANCEL:
        break;
    }
    return true;
  }
}