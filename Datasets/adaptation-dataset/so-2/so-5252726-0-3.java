public class foo {
public static Bitmap getRoundedCornerBitmap(Context context, Bitmap input, int pixels , int w , int h , boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR  ) {

    Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
    Canvas canvas = new Canvas(output);
    final float densityMultiplier = context.getResources().getDisplayMetrics().density;

    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, w, h);
    final RectF rectF = new RectF(rect);

    //make sure that our rounded corner is scaled appropriately
    final float roundPx = pixels*densityMultiplier;

    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);


    //draw rectangles over the corners we want to be square
    if (squareTL ){
        canvas.drawRect(0, h/2, w/2, h, paint);
    }
    if (squareTR ){
        canvas.drawRect(w/2, h/2, w, h, paint);
    }
    if (squareBL ){
        canvas.drawRect(0, 0, w/2, h/2, paint);
    }
    if (squareBR ){
        canvas.drawRect(w/2, 0, w, h/2, paint);
    }


    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(input, 0,0, paint);

    return output;
}
}