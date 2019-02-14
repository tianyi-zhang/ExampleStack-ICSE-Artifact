public class foo{
    private static boolean onTouchEvent(View view, MotionEvent event) {
        TextView widget = (TextView) view;
        Object text = widget.getText();
        if (text instanceof Spanned) {
            Spanned buffer = (Spanned) text;

            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        if (buffer instanceof Spannable) {
                            Selection.setSelection( (Spannable) buffer,
                                    buffer.getSpanStart(link[0]),
                                    buffer.getSpanEnd(link[0]));
                        }
                    }
                    return true;
                }
            }

        }

        return false;
    }
}