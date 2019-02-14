public class foo{
    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        boolean handled = false;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            pressedSpan = getPressedSpan(textView, spannable, event);
            if (pressedSpan != null) {
                pressedSpan.setPressed(true);
                Selection.setSelection(spannable, spannable.getSpanStart(pressedSpan),
                        spannable.getSpanEnd(pressedSpan));
                handled = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            TouchableUrlSpan touchedSpan = getPressedSpan(textView, spannable, event);
            if (pressedSpan != null && touchedSpan != pressedSpan) {
                pressedSpan.setPressed(false);
                pressedSpan = null;
                Selection.removeSelection(spannable);
            }
        } else {
            if (pressedSpan != null) {
                pressedSpan.setPressed(false);
                super.onTouchEvent(textView, spannable, event);
                handled = true;
            }
            pressedSpan = null;
            Selection.removeSelection(spannable);
        }
        return handled;
    }
}