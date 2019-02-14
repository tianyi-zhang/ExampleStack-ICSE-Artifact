public class foo{
  // Taken from http://stackoverflow.com/a/10383164/902599
  private int getScreenOrientation() {
    int rotation;
    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.GINGERBREAD) {
        rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
    } else {
        rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getOrientation();
    }
    DisplayMetrics dm = new DisplayMetrics();
    ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    int orientation;
    // if the device's natural orientation is portrait:
    if ((rotation == Surface.ROTATION_0
            || rotation == Surface.ROTATION_180) && height > width ||
            (rotation == Surface.ROTATION_90
                    || rotation == Surface.ROTATION_270) && width > height) {
      switch(rotation) {
        case Surface.ROTATION_0:
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        case Surface.ROTATION_180:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        case Surface.ROTATION_270:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        default:
          Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                  "portrait.");
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
      }
    }
    // if the device's natural orientation is landscape or if the device
    // is square:
    else {
      switch(rotation) {
        case Surface.ROTATION_0:
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
        case Surface.ROTATION_90:
          orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
          break;
        case Surface.ROTATION_180:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
          break;
        case Surface.ROTATION_270:
          orientation =
                  ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
          break;
        default:
          Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                  "landscape.");
          orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
          break;
      }
    }
    Log.v(TAG, "Orientation: " + orientation);
    return orientation;
  }
}