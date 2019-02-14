public class foo {
public static void drawTextOnCanvasWithMagnifier(Canvas canvas, String text, float x, float y, Paint paint) {
        if (android.os.Build.VERSION.SDK_INT <= 15) {
            //draw normally
            canvas.drawText(text, x, y, paint);
        }
        else {
            //workaround
            float originalTextSize = paint.getTextSize();
            final float magnifier = 1000f;
            canvas.save();
            canvas.scale(1f / magnifier, 1f / magnifier);
            paint.setTextSize(originalTextSize * magnifier);
            canvas.drawText(text, x * magnifier, y * magnifier, paint);
            canvas.restore();
            paint.setTextSize(originalTextSize);
        }
    }
}