public class foo{
    // some callback - currently unused
    public static void onRequestPermissionsResult(Context parentActivity,
                                                  int requestCode,
                                                  String[] permissions,
                                                  int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use
                    // the features what required the permission
                }
                else
                {
                    Toast.makeText(parentActivity, "The app was not allowed " +
                            "to write to your storage. Hence, it cannot " +
                            "function properly. Please consider granting it " +
                            "this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}