public class foo {
public static void setListViewHeightBasedOnChildren(ListView listView)
{
    ListAdapter listAdapter = listView.getAdapter();
    if(listAdapter == null) return;
    if(listAdapter.getCount() <= 1) return;

    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
    int totalHeight = 0;
    View view = null;
    for(int i = 0; i < listAdapter.getCount(); i++)
    {
        view = listAdapter.getView(i, view, listView);
        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
        totalHeight += view.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
    listView.requestLayout();
}
}