public class foo {
public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
    int childCount = viewGroup.getChildCount();
    for (int i = 0; i < childCount; i++) {
        View view = viewGroup.getChildAt(i);
        if(view.isFocusable())
            view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            enableDisableViewGroup((ViewGroup) view, enabled);
            } else if (view instanceof ListView) {
                if(view.isFocusable())
                    view.setEnabled(enabled);
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    if(view.isFocusable())
                        listView.getChildAt(j).setEnabled(false);
                    }
                }
        }
    }
}