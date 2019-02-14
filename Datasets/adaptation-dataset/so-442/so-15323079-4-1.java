public class foo {
    private void refitText(String text, int textWidth, int textHeight)
    {
        if (textWidth <= 0)
            return;
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
        int targetHeight = textHeight - this.getPaddingTop() - this.getPaddingBottom();
        float hi = maxFontSize;
        float lo = 2;
//      final float threshold = 0.5f; // How close we have to be
        final float threshold = 1f; // How close we have to be

        mTestPaint.set(this.getPaint());

        Rect bounds = new Rect();

        while ((hi - lo) > threshold)
        {
            float size = (hi + lo) / 2;
            mTestPaint.setTextSize(size);

            mTestPaint.getTextBounds(text, 0, text.length(), bounds);

            if (bounds.width() >= targetWidth || bounds.height() >= targetHeight)
                hi = size; // too big
            else
                lo = size; // too small

//          if (mTestPaint.measureText(text) >= targetWidth)
//              hi = size; // too big
//          else
//              lo = size; // too small
        }
        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }
}