public class foo{
	private void prepCamera(){
		String storageState = Environment.getExternalStorageState();
        if(storageState.equals(Environment.MEDIA_MOUNTED)) {
        	// http://stackoverflow.com/a/5054673/974800
            String path = Calculator.getDefaultFilePath()  
            		+ "files/" + System.currentTimeMillis() + ".jpg";
            _photoFile = new File(path);
            try {
                if(_photoFile.exists() == false) {
                    _photoFile.getParentFile().mkdirs();
                    _photoFile.createNewFile();
                }

            } catch (IOException e) {
                Log.e(TAG, "Could not create file.", e);
            }
            Log.i(TAG, path);

            _fileUri = Uri.fromFile(_photoFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE );
            intent.putExtra( MediaStore.EXTRA_OUTPUT, _fileUri);
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }   else {
            new AlertDialog.Builder(PindActivity.this)
            .setMessage("External Storeage (SD Card) is required.\n\nCurrent state: " + storageState)
            .setCancelable(true).create().show();
        }
	}
}