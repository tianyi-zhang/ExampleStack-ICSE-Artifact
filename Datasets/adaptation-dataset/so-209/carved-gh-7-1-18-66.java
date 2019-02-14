public class foo{
  @Override
  public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec,
      int heightSpec) {
    final int widthMode = View.MeasureSpec.getMode(widthSpec);
    final int heightMode = View.MeasureSpec.getMode(heightSpec);
    final int widthSize = View.MeasureSpec.getSize(widthSpec);
    final int heightSize = View.MeasureSpec.getSize(heightSpec);
    int width = 0;
    int height = 0;
    for (int i = 0; i < getItemCount(); i++) {
      measureScrapChild(recycler, i,
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
          View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED), measuredDimension);

      if (getOrientation() == HORIZONTAL) {
        width = width + measuredDimension[0];
        if (i == 0) {
          height = measuredDimension[1];
        }
      } else {
        height = height + measuredDimension[1];
        if (i == 0) {
          width = measuredDimension[0];
        }
      }
    }

    // If child view is more than screen size, there is no need to make it wrap content. We can use original onMeasure() so we can scroll view.
    if (height < heightSize && width < widthSize) {

      switch (widthMode) {
        case View.MeasureSpec.EXACTLY:
          width = widthSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
      }

      switch (heightMode) {
        case View.MeasureSpec.EXACTLY:
          height = heightSize;
        case View.MeasureSpec.AT_MOST:
        case View.MeasureSpec.UNSPECIFIED:
      }

      setMeasuredDimension(width, height);
    } else {
      super.onMeasure(recycler, state, widthSpec, heightSpec);
    }
  }
}