public class foo {
public static void getTotalHeightofListView(ListView listView) {

    ListAdapter mAdapter = listView.getAdapter();

    int totalHeight = 0;

    for (int i = 0; i < mAdapter.getCount(); i++) {
        View mView = mAdapter.getView(i, null, listView);

        mView.measure(
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),

                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        totalHeight += mView.getMeasuredHeight();
        Log.w("HEIGHT" + i, String.valueOf(totalHeight));

    }

    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight
            + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();

}
}