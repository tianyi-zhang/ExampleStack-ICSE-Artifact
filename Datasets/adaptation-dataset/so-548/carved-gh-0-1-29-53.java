public class foo{
    @Override
    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mCustomClickableSpan = getPressedSpan(textView, spannable, event);
            if (mCustomClickableSpan != null) {
                mCustomClickableSpan.setPressed(true);
                Selection.setSelection(spannable, spannable.getSpanStart(mCustomClickableSpan), spannable.getSpanEnd(mCustomClickableSpan));
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            CustomClickableSpan touchedSpan = getPressedSpan(textView, spannable, event);
            if (mCustomClickableSpan != null && touchedSpan != mCustomClickableSpan) {
                mCustomClickableSpan.setPressed(false);
                mCustomClickableSpan = null;
                Selection.removeSelection(spannable);
            }
        } else {
            if (mCustomClickableSpan != null) {
                mCustomClickableSpan.setPressed(false);
                super.onTouchEvent(textView, spannable, event);
            }
            mCustomClickableSpan = null;
            Selection.removeSelection(spannable);
        }
        return true;
    }
}