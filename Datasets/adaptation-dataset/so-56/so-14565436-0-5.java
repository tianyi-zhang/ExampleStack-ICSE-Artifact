public class foo {
public static void lockOrientation(Activity activity) {
    Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    int rotation = display.getRotation();
    int tempOrientation = activity.getResources().getConfiguration().orientation;
    int orientation = 0;
    switch(tempOrientation)
    {
    case Configuration.ORIENTATION_LANDSCAPE:
        if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90)
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        else
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        break;
    case Configuration.ORIENTATION_PORTRAIT:
        if(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_270)
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        else
            orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
    }
    activity.setRequestedOrientation(orientation);
}
}