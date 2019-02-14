public class foo{
    @Override
    public boolean askForOk(String data){
    	//Code taken from http://stackoverflow.com/questions/2028697/dialogs-alertdialogs-how-to-block-execution-while-dialog-is-up-net-style
    	// make a handler that throws a runtime exception when a message is received
    	final HandlerClass handler = new HandlerClass();

        // make a text input dialog and show it
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        
        alert.setTitle("Permission request");
        alert.setMessage(data);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                askForOkResult = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	askForOkResult = false;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert.show();

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        return askForOkResult;
    }
}