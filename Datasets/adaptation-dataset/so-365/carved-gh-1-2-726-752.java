public class foo{
	// Workaround to fix missing text on Lollipop and above,
	// and probably some rendering issues with Jelly Bean and above
	// Modified from http://stackoverflow.com/a/14989037/746068
	public static void drawTextOnCanvasWithMagnifier(Canvas canvas, String text, float x, float y, Paint paint) {
		if (android.os.Build.VERSION.SDK_INT <= 15) {
			//draw normally
			canvas.drawText(text, x, y, paint);
		}
		else {
			//workaround
			float originalStrokeWidth = paint.getStrokeWidth();
			float originalTextSize = paint.getTextSize();
			final float magnifier = 1000f;

			canvas.save();
			canvas.scale(1f / magnifier, 1f / magnifier);

			paint.setTextSize(originalTextSize * magnifier);
			paint.setStrokeWidth(originalStrokeWidth * magnifier);

			canvas.drawText(text, x * magnifier, y * magnifier, paint);
			canvas.restore();

			paint.setTextSize(originalTextSize);
			paint.setStrokeWidth(originalStrokeWidth);
		}
	}
}