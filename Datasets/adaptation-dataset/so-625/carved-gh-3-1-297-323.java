public class foo{
    /**
     * http://stackoverflow.com/questions/2257963/android-how-to-show-dialog-to-confirm-user-wishes-to-exit-activity
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(mSettings.isConfirmQuitOrLogout() && keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
            //Ask the user if they want to quit
            new AlertDialog.Builder(new ContextThemeWrapper(this, mSettings.getDialogTheme()))
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.really_quit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Stop the activity
                    finish();    
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();

            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }
}