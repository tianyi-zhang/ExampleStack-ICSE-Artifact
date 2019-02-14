public class foo {
/**
 * Get the TextView height before the TextView will render
 * @param textView the TextView to measure
 * @return the height of the textView
 */
public static int getTextViewHeight(TextView textView) {
    WindowManager wm =
            (WindowManager) textView.getContext().getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();

    int deviceWidth;

    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
    } else {
        deviceWidth = display.getWidth();
    }

    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    textView.measure(widthMeasureSpec, heightMeasureSpec);
    return textView.getMeasuredHeight();
}
}