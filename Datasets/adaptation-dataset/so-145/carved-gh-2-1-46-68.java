public class foo{
    private NoteBlockClickableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        Layout layout = textView.getLayout();
        int line = layout.getLineForVertical(y);
        int off = layout.getOffsetForHorizontal(line, x);

        NoteBlockClickableSpan[] link = spannable.getSpans(off, off, NoteBlockClickableSpan.class);
        NoteBlockClickableSpan touchedSpan = null;
        if (link.length > 0) {
            touchedSpan = link[0];
        }

        return touchedSpan;
    }
}