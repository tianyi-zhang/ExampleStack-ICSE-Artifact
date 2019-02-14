public class foo {
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
private void lockOrientation() {
    Display display = activity.getWindowManager().getDefaultDisplay();
    int rotation = display.getRotation();
    int height;
    int width;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
        height = display.getHeight();
        width = display.getWidth();
    } else {
        Point size = new Point();
        display.getSize(size);
        height = size.y;
        width = size.x;
    }
    switch (rotation) {
    case Surface.ROTATION_90:
        if (width > height)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            activity.setRequestedOrientation(9/* reversePortait */);
        break;
    case Surface.ROTATION_180:
        if (height > width)
            activity.setRequestedOrientation(9/* reversePortait */);
        else
            activity.setRequestedOrientation(8/* reverseLandscape */);
        break;          
    case Surface.ROTATION_270:
        if (width > height)
            activity.setRequestedOrientation(8/* reverseLandscape */);
        else
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        break;
    default :
        if (height > width)
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
}
}