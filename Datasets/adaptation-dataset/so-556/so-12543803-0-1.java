public class foo {
public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
            Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(output);

    final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips,
            context.getResources().getDisplayMetrics());
    final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips,
            context.getResources().getDisplayMetrics());
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);

    // prepare canvas for transfer
    paint.setAntiAlias(true);
    paint.setColor(0xFFFFFFFF);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawARGB(0, 0, 0, 0);
    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

    // draw bitmap
    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);

    // draw border
    paint.setColor(color);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth((float) borderSizePx);
    canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

    return output;
}
}