public class foo {
    private void refitText(String text, int textWidth) {

        if (textWidth <= 0 || text.isEmpty())
            return;

        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

        // this is most likely a non-relevant call
        if( targetWidth<=2 )
            return;

        // text already fits with the xml-defined font size?
        mTestPaint.set(this.getPaint());
        mTestPaint.setTextSize(defaultTextSize);
        if(mTestPaint.measureText(text) <= targetWidth) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultTextSize);
            return;
        }

        // adjust text size using binary search for efficiency
        float hi = defaultTextSize;
        float lo = 2;
        final float threshold = 0.5f; // How close we have to be
        while (hi - lo > threshold) {
            float size = (hi + lo) / 2;
            mTestPaint.setTextSize(size);
            if(mTestPaint.measureText(text) >= targetWidth ) 
                hi = size; // too big
            else 
                lo = size; // too small

        }

        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }
}