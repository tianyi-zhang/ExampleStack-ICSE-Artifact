public class foo{
    private void drawVertical(Canvas c, RecyclerView parent) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int lastDecoratedChild = getLastDecoratedChild(parent);
        for (int i = 0; i < lastDecoratedChild; i++) {
            final View child = parent.getChildAt(i);
            final int ty = (int) (child.getTranslationY() + 0.5f);
            final int tx = (int) (child.getTranslationX() + 0.5f);
            final int bottom = manager.getDecoratedBottom(child) + ty;
            final int top = bottom - divider.getIntrinsicHeight();
            divider.setBounds(left + tx, top, right + tx, bottom);
            divider.draw(c);
        }
    }
}