public class foo{
    /************************************************************************************
    *   needed because else the nested preference screen don't have a actionbar/toolbar *
    *   see the fix and the given problem here: http://stackoverflow.com/a/27455363     *
    ************************************************************************************/
    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        //ViewGroup list;
        Toolbar bar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //list = (ViewGroup) dialog.findViewById(android.R.id.list);
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);
            //list = content;
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
        //list.addView(detailsPrefScreenToAdd.getStatusViewGroup(), 1); //TODO
        bar.setTitle(preferenceScreen.getTitle());
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogI) {
                if (AppData.getLoginSuccessful()) {
                    dialogI.dismiss();
                } else {
                    showNotConnectedDialog(dialog);
                }
            }
        });
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppData.getLoginSuccessful()) {
                    dialog.dismiss();
                } else {
                    showNotConnectedDialog(dialog);
                }
            }
        });
    }
}