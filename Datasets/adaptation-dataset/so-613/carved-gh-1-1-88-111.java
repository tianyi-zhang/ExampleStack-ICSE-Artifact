public class foo{
  @Override
  protected void onLayout(boolean changed, int leftPosition, int topPosition, int rightPosition, int bottomPosition) {
    final int widthOffset = getMeasuredWidth() - getPaddingRight();
    int x = getPaddingLeft();
    int y = getPaddingTop();

    Iterator<RowMeasurement> rowIterator = currentRows.iterator();
    RowMeasurement currentRow = rowIterator.next();
    for (View child : getLayoutChildren()) {
      final int childWidth = child.getMeasuredWidth();
      final int childHeight = child.getMeasuredHeight();
      if (x + childWidth > widthOffset) {
        x = getPaddingLeft();
        y += currentRow.height + verticalSpacing;
        if (rowIterator.hasNext()) {
          currentRow = rowIterator.next();
        }
      }
      // Align the child vertically.
      int childY = y + (currentRow.height - childHeight) / 2;
      child.layout(x, childY, x + childWidth, childY + childHeight);
      x += childWidth + horizontalSpacing;
    }
  }
}