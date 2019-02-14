public class foo{
  // http://stackoverflow.com/a/30572151/2104686
  @Override
  public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    int action = event.getAction();

    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
      int eventX = (int) event.getX();
      int eventY = (int) event.getY();

      eventX -= widget.getTotalPaddingLeft();
      eventY -= widget.getTotalPaddingTop();

      eventX += widget.getScrollX();
      eventY += widget.getScrollY();

      Layout layout = widget.getLayout();
      int line = layout.getLineForVertical(eventY);
      int off = layout.getOffsetForHorizontal(line, eventX);

      ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

      if (link.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          link[0].onClick(widget);
        } else {
          Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
        }
        return true;
      }
    }

    return Touch.onTouchEvent(widget, buffer, event);
  }
}