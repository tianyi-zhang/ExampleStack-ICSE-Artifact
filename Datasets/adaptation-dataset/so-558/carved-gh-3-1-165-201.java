public class foo{
    /**
     * Enable toolbar on pref screen
     * http://stackoverflow.com/a/30281205
     */
    private void setupActionBar() {
        Toolbar toolbar = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                ViewGroup root = (ViewGroup) findViewById(android.R.id.list).getParent().getParent().getParent();
                toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                root.addView(toolbar, 0);
            } else {
                ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
                if (root.getChildAt(0) instanceof ListView) {
                    ListView content = (ListView) root.getChildAt(0);
                    root.removeAllViews();
                    toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                    int height;
                    TypedValue tv = new TypedValue();
                    if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                        height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                    } else {
                        height = toolbar.getHeight();
                    }
                    content.setPadding(0, height, 0, 0);
                    root.addView(content);
                    root.addView(toolbar);
                }
            }
        } catch (Exception ex) {
            Log.w(TAG, "Failed to get Toolbar on Settings Page", ex);
        }
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}