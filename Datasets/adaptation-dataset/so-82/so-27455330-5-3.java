public class foo {
public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
    final Dialog dialog = preferenceScreen.getDialog();

    Toolbar bar;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
        root.addView(bar, 0); // insert at top
    } else {
        ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
        ListView content = (ListView) root.getChildAt(0);

        root.removeAllViews();

        bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

        int height;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }else{
            height = bar.getHeight();
        }

        content.setPadding(0, height, 0, 0);

        root.addView(content);
        root.addView(bar);
    }

    bar.setTitle(preferenceScreen.getTitle());

    bar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    });
}
}