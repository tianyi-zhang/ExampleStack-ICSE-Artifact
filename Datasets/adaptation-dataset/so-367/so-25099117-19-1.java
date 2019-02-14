public class foo {
    /**
     * Resize the text size with specified width and height
     * 
     * @param width
     * @param height
     */
    public void resizeText(int width, int height) {
        CharSequence text = getText();

        // Do not resize if the view does not have dimensions or there is no
        // text
        if (text == null || text.length() == 0 || height <= 0 || width <= 0
                || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();

        // Bisection method: fast & precise
        float lower = mMinTextSize;
        float upper = mMaxTextSize;
        int loop_counter = 1;
        float targetTextSize = (lower + upper) / 2;
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        int textWidth = getTextWidth(text, textPaint, width, targetTextSize);

        while (loop_counter < BISECTION_LOOP_WATCH_DOG && upper - lower > 1) {
            targetTextSize = (lower + upper) / 2;
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            textWidth = getTextWidth(text, textPaint, width, targetTextSize);
            if (textHeight > (height) || textWidth > (width))
                upper = targetTextSize;
            else
                lower = targetTextSize;
            loop_counter++;
        }

        targetTextSize = lower;
        textHeight = getTextHeight(text, textPaint, width, targetTextSize);

        // If we had reached our minimum text size and still don't fit, append
        // an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize
                && textHeight > height) {
            // Draw using a static layout
            // modified: use a copy of TextPaint for measuring
            TextPaint paintCopy = new TextPaint(textPaint);
            paintCopy.setTextSize(targetTextSize);
            StaticLayout layout = new StaticLayout(text, paintCopy, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            // Check that we have a least one line of rendered text
            if (layout.getLineCount() > 0) {
                // Since the line at the specific vertical position would be cut
                // off,
                // we must trim up to the previous line
                int lastLine = layout.getLineForVertical(height) - 1;
                // If the text would not even fit on a single line, clear it
                if (lastLine < 0) {
                    setText("");
                }
                // Otherwise, trim to the previous line and add an ellipsis
                else {
                    int start = layout.getLineStart(lastLine);
                    int end = layout.getLineEnd(lastLine);
                    float lineWidth = layout.getLineWidth(lastLine);
                    float ellipseWidth = paintCopy.measureText(mEllipsis);

                    // Trim characters off until we have enough room to draw the
                    // ellipsis
                    while (width < lineWidth + ellipseWidth) {
                        lineWidth = paintCopy.measureText(text.subSequence(
                                start, --end + 1).toString());
                    }
                    setText(text.subSequence(0, end) + mEllipsis);
                }
            }
        }

        // Some devices try to auto adjust line spacing, so force default line
        // spacing
        // and invalidate the layout as a side effect
        setTextSize(TypedValue.COMPLEX_UNIT_PX, targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }
}