public class foo{
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    final int maxInternalWidth = MeasureSpec.getSize(widthMeasureSpec) - getHorizontalPadding();
    final int maxInternalHeight = MeasureSpec.getSize(heightMeasureSpec) - getVerticalPadding();
    List<RowMeasurement> rows = new ArrayList<RowMeasurement>();
    RowMeasurement currentRow = new RowMeasurement(maxInternalWidth, widthMode);
    rows.add(currentRow);
    for (View child : getLayoutChildren()) {
      LayoutParams childLayoutParams = child.getLayoutParams();
      int childWidthSpec = createChildMeasureSpec(childLayoutParams.width, maxInternalWidth, widthMode);
      int childHeightSpec = createChildMeasureSpec(childLayoutParams.height, maxInternalHeight, heightMode);
      child.measure(childWidthSpec, childHeightSpec);
      int childWidth = child.getMeasuredWidth();
      int childHeight = child.getMeasuredHeight();
      if (currentRow.wouldExceedMax(childWidth)) {
        currentRow = new RowMeasurement(maxInternalWidth, widthMode);
        rows.add(currentRow);
      }
      currentRow.addChildDimensions(childWidth, childHeight);
    }

    int longestRowWidth = 0;
    int totalRowHeight = 0;
    for (int index = 0; index < rows.size(); index++) {
      RowMeasurement row = rows.get(index);
      totalRowHeight += row.getHeight();
      if (index < rows.size() - 1) {
        totalRowHeight += verticalSpacing;
      }
      longestRowWidth = Math.max(longestRowWidth, row.getWidth());
    }
    setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(widthMeasureSpec) : longestRowWidth
        + getHorizontalPadding(), heightMode == MeasureSpec.EXACTLY ? MeasureSpec.getSize(heightMeasureSpec)
        : totalRowHeight + getVerticalPadding());
    currentRows = Collections.unmodifiableList(rows);
  }
}