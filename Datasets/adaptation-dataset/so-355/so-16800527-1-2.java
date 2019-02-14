public class foo {
/** Sets up the action bar for an {@link PreferenceScreen} */
public static void initializeActionBar(PreferenceScreen preferenceScreen) {
    final Dialog dialog = preferenceScreen.getDialog();

    if (dialog != null) {
        // Inialize the action bar
        dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

        // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
        // events instead of passing to the activity
        // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
        View homeBtn = dialog.findViewById(android.R.id.home);

        if (homeBtn != null) {
            OnClickListener dismissDialogClickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            };

            // Prepare yourselves for some hacky programming
            ViewParent homeBtnContainer = homeBtn.getParent();

            // The home button is an ImageView inside a FrameLayout
            if (homeBtnContainer instanceof FrameLayout) {
                ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

                if (containerParent instanceof LinearLayout) {
                    // This view also contains the title text, set the whole view as clickable
                    ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
                } else {
                    // Just set it on the home button
                    ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
                }
            } else {
                // The 'If all else fails' default case
                homeBtn.setOnClickListener(dismissDialogClickListener);
            }
        }    
    }
}
}