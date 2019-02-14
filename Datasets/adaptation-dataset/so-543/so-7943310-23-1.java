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
        // or if mTextSize has not been initialized
        if (text == null || text.length() == 0 || height <= 0 || width <= 0
                || mTextSize == 0) {
            return;
        }

        // Get the text view's paint object
        TextPaint textPaint = getPaint();

        // Store the current text size
        float oldTextSize = textPaint.getTextSize();

        // If there is a max text size set, use the lesser of that and the
        // default text size
        float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize, mMaxTextSize)
                : mTextSize;

        // Get the required text height
        int textHeight = getTextHeight(text, textPaint, width, targetTextSize);
        int textWidth = getTextWidth(text, textPaint, width, targetTextSize);

        // Until we either fit within our text view or we had reached our min
        // text size, incrementally try smaller sizes
        while (((textHeight > height) || (textWidth > width))
                && targetTextSize > mMinTextSize) {
            targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, width, targetTextSize);
            textWidth = getTextWidth(text, textPaint, width, targetTextSize);
        }

        // If we had reached our minimum text size and still don't fit, append
        // an ellipsis
        if (mAddEllipsis && targetTextSize == mMinTextSize && textHeight > height) {
            // Draw using a static layout
            StaticLayout layout = new StaticLayout(text, textPaint, width,
                    Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
            layout.draw(sTextResizeCanvas);
            int lastLine = layout.getLineForVertical(height) - 1;
            int start = layout.getLineStart(lastLine);
            int end = layout.getLineEnd(lastLine);
            float lineWidth = layout.getLineWidth(lastLine);
            float ellipseWidth = textPaint.measureText(mEllipsis);

            // Trim characters off until we have enough room to draw the
            // ellipsis
            while (width < lineWidth + ellipseWidth) {
                lineWidth = textPaint.measureText(text.subSequence(start, --end + 1)
                        .toString());
            }
            setText(text.subSequence(0, end) + mEllipsis);

        }

        // Some devices try to auto adjust line spacing, so force default line
        // spacing
        // and invalidate the layout as a side effect
        textPaint.setTextSize(targetTextSize);
        setLineSpacing(mSpacingAdd, mSpacingMult);

        // Notify the listener if registered
        if (mTextResizeListener != null) {
            mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
        }

        // Reset force resize flag
        mNeedsResize = false;
    }
}