public class foo {
public void foo(){
String storageState = Environment.getExternalStorageState();
        if(storageState.equals(Environment.MEDIA_MOUNTED)) {

            String path = Environment.getExternalStorageDirectory().getName() + File.separatorChar + "Android/data/" + MainActivity.this.getPackageName() + "/files/" + md5(upc) + ".jpg";
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
            startActivityForResult(intent, TAKE_PICTURE);
        }   else {
            new AlertDialog.Builder(MainActivity.this)
            .setMessage("External Storeage (SD Card) is required.\n\nCurrent state: " + storageState)
            .setCancelable(true).create().show();
        }
}
}