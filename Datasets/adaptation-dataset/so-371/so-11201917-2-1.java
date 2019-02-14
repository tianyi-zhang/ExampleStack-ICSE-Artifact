public class foo {
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
            final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
            int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
            final int count = getChildCount();
            int xpos = getPaddingLeft();
            int ypos = getPaddingTop();
            int childHeightMeasureSpec;
            if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
            else
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            mHeight = 0;
            for(int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if(child.getVisibility() != GONE) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec);
                    final int childw = child.getMeasuredWidth();
                    mHeight = Math.max(mHeight, child.getMeasuredHeight() + PAD_V);
                    if(xpos + childw > width) {
                        xpos = getPaddingLeft();
                        ypos += mHeight;
                    }
                    xpos += childw + PAD_H;
                }
            }
            if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
                height = ypos + mHeight;
            } else if(MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
                if(ypos + mHeight < height) {
                    height = ypos + mHeight;
                }
            }
            height += 5; // Fudge to avoid clipping bottom of last row.
            setMeasuredDimension(width, height);
        } // end onMeasure()
}