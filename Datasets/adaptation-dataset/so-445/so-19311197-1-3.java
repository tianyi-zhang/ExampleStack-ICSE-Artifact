public class foo {
/**** Method for Setting the Height of the ListView dynamically.
 **** Hack to fix the issue of not showing all the items of the ListView
 **** when placed inside a ScrollView  ****/
public static void setListViewHeightBasedOnChildren(ListView listView) {
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter == null)
        return;

    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
    int totalHeight = 0;
    View view = null;
    for (int i = 0; i < listAdapter.getCount(); i++) {
        view = listAdapter.getView(i, view, listView);
        if (i == 0)
            view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
        totalHeight += view.getMeasuredHeight();
    }
    ViewGroup.LayoutParams params = listView.getLayoutParams();
    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    listView.setLayoutParams(params);
}
}