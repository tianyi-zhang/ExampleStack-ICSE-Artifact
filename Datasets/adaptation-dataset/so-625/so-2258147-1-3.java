public class foo {
@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
    //Handle the back button
    if(keyCode == KeyEvent.KEYCODE_BACK) {
        //Ask the user if they want to quit
        new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.quit)
        .setMessage(R.string.really_quit)
        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Stop the activity
                YourClass.this.finish();    
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