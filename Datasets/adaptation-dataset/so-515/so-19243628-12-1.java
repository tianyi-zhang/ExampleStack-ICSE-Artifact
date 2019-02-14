public class foo {
/**
 * Resize the text size with specified width and height
 * 
 * @param width
 * @param height
 */
public void resizeText(boolean increase) {
    CharSequence text = getText();
    // Do not resize if the view does not have dimensions or there is no
    // text
    if (text == null || text.length() == 0 || heightLimit <= 0 || widthLimit <= 0 || mTextSize == 0) {
        return;
    }

    // Get the text view's paint object
    TextPaint textPaint = getPaint();

    // Get the required text height
    int textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);


    // If the text length is increased 
    // Until we either fit within our text view or we had reached our min
    // text size, incrementally try smaller sizes
    if (increase) {
        while (textHeight > heightLimit && mTextSize > mMinTextSize) {
            mTextSize = Math.max(mTextSize - 2, mMinTextSize);
            textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
        }
    } 
//      text length has been decreased
    else {
//          if max test size is set then add it to while condition
        if (mMaxTextSize != 0) {
            while (textHeight < heightLimit && mTextSize <= mMaxTextSize) {
                mTextSize = mTextSize + 2;
                textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
            }
        } else {
            while (textHeight < heightLimit) {
                mTextSize = mTextSize + 2;
                textHeight = getTextHeight(text, textPaint, widthLimit, mTextSize);
            }
        }
        mTextSize = textHeight > heightLimit ? mTextSize - 2 : mTextSize;
    }

    // If we had reached our minimum text size and still don't fit, append
    // an ellipsis
    if (mAddEllipsis && mTextSize == mMinTextSize && textHeight > heightLimit) {
        // Draw using a static layout
        TextPaint paint = new TextPaint(textPaint);
        StaticLayout layout = new StaticLayout(text, paint, widthLimit, Alignment.ALIGN_NORMAL, mSpacingMult,
                mSpacingAdd, false);
        // Check that we have a least one line of rendered text
        if (layout.getLineCount() > 0) {
            // Since the line at the specific vertical position would be cut
            // off,
            // we must trim up to the previous line
            int lastLine = layout.getLineForVertical(heightLimit) - 1;
            // If the text would not even fit on a single line, clear it
            if (lastLine < 0) {
                setText("");
            }
            // Otherwise, trim to the previous line and add an ellipsis
            else {
                int start = layout.getLineStart(lastLine);
                int end = layout.getLineEnd(lastLine);
                float lineWidth = layout.getLineWidth(lastLine);
                float ellipseWidth = paint.measureText(mEllipsis);

                // Trim characters off until we have enough room to draw the
                // ellipsis
                while (widthLimit < lineWidth + ellipseWidth) {
                    lineWidth = paint.measureText(text.subSequence(start, --end + 1).toString());
                }
                setText(text.subSequence(0, end) + mEllipsis);
            }
        }
    }

    // Some devices try to auto adjust line spacing, so force default line
    // spacing
    // and invalidate the layout as a side effect
    setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
    setLineSpacing(mSpacingAdd, mSpacingMult);

}
}