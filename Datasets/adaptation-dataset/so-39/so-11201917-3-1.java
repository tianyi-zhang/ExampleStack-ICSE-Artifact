public class foo {
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            final int width = r - l;
            int xpos = getPaddingLeft();
            int ypos = getPaddingTop();
            for(int i = 0; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if(child.getVisibility() != GONE) {
                    final int childw = child.getMeasuredWidth();
                    final int childh = child.getMeasuredHeight();
                    if(xpos + childw > width) {
                        xpos = getPaddingLeft();
                        ypos += mHeight;
                    }
                    child.layout(xpos, ypos, xpos + childw, ypos + childh);
                    xpos += childw + PAD_H;
                }
            }
        } // end onLayout()
}