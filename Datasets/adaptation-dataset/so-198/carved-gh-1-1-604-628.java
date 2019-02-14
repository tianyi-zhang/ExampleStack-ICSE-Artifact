public class foo{
	 */
	public static void positionToast(Toast toast, View view, Window window, int offsetX, int offsetY) {
		// toasts are positioned relatively to decor view, views relatively to their parents, we have to gather additional data to have a common coordinate system
		Rect rect = new Rect();
		window.getDecorView().getWindowVisibleDisplayFrame(rect);
		// covert anchor view absolute position to a position which is relative to decor view
		int[] viewLocation = new int[2];
		view.getLocationInWindow(viewLocation);
		int viewLeft = viewLocation[0] - rect.left;
		int viewTop = viewLocation[1] - rect.top;
		
		// measure toast to center it relatively to the anchor view
		DisplayMetrics metrics = new DisplayMetrics();
		window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.UNSPECIFIED);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.UNSPECIFIED);
		toast.getView().measure(widthMeasureSpec, heightMeasureSpec);
		int toastWidth = toast.getView().getMeasuredWidth();
		
		// compute toast offsets
		int toastX = viewLeft + (view.getWidth() / 2 - toastWidth) + offsetX;
		int toastY = viewTop + view.getHeight() + offsetY;
		
		toast.setGravity(Gravity.LEFT | Gravity.TOP, toastX, toastY);
	}
}