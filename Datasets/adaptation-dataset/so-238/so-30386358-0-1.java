public class foo {
private void drawVertical(Canvas c, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();
    final int childCount = parent.getChildCount();
    final int dividerHeight = mDivider.getIntrinsicHeight();

    for (int i = 1; i < childCount; i++) {
        final View child = parent.getChildAt(i);
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
        final int ty = (int)(child.getTranslationY() + 0.5f);
        final int top = child.getTop() - params.topMargin + ty;
        final int bottom = top + dividerHeight;
        mDivider.setBounds(left, top, right, bottom);
        mDivider.draw(c);
    }
}
}