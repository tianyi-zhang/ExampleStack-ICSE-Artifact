public class foo{
    /**
     * Enable toolbar on child screen
     * http://stackoverflow.com/a/27455330
     *
     * @param preferenceScreen
     */
    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        Toolbar bar = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                View tempView = dialog.findViewById(android.R.id.list);
                ViewParent viewParent = tempView.getParent();
                if (viewParent != null && viewParent instanceof LinearLayout) {
                    LinearLayout root = (LinearLayout) viewParent;
                    bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                    root.addView(bar, 0); // insert at top
                } else
                    Log.i(TAG, "setUpNestedScreen() using unknown Layout: " + viewParent.getClass().toString());
            } else {
                ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
                ListView content = (ListView) root.getChildAt(0);

                root.removeAllViews();

                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

                int height;
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                } else {
                    height = bar.getHeight();
                }

                content.setPadding(0, height, 0, 0);

                root.addView(content);
                root.addView(bar);
            }
        } catch (Exception ex) {
            Log.w(TAG, "Failed to get Toolbar on Settings Page", ex);
        }

        if (bar != null) {
            bar.setTitle(preferenceScreen.getTitle());
            bar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}