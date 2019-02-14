public class foo {
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onTestSize(int suggestedSize, RectF availableSPace) {
        mPaint.setTextSize(suggestedSize);
        String text = getText().toString();
        boolean singleline = getMaxLines() == 1;
        if (singleline) {
            mTextRect.bottom = mPaint.getFontSpacing();
            mTextRect.right = mPaint.measureText(text);
        } else {
            StaticLayout layout = new StaticLayout(text, mPaint,
                    mWidthLimit, Alignment.ALIGN_NORMAL, mSpacingMult,
                    mSpacingAdd, true);
            // return early if we have more lines
            if (getMaxLines() != NO_LINE_LIMIT
                    && layout.getLineCount() > getMaxLines()) {
                return 1;
            }
            mTextRect.bottom = layout.getHeight();
            int maxWidth = -1;
            for (int i = 0; i < layout.getLineCount(); i++) {
                if (maxWidth < layout.getLineWidth(i)) {
                    maxWidth = (int) layout.getLineWidth(i);
                }
            }
            mTextRect.right = maxWidth;
        }

        mTextRect.offsetTo(0, 0);
        if (availableSPace.contains(mTextRect)) {
            // may be too small, don't worry we will find the best match
            return -1;
        } else {
            // too big
            return 1;
        }
    }
}