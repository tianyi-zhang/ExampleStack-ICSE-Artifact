public class foo{
    /*
     * Code taken from http://stackoverflow.com/questions/19908003/getting-height-of-text-view-before-rendering-to-layout
     *
     * by support_ms and Hugo Gresse
     */
    public static int getMeasuredHeight(View view, float widthRatio) {
        WindowManager windowManager =
                (WindowManager) view.getContext()
                        .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int deviceWidth = (int) (size.x * widthRatio);

        Log.d(TAG, "deviceWidth: " + deviceWidth);

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth,
                View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();
    }
}