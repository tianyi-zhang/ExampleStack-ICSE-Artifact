package edu.cornell.opencomm.view;

import edu.cornell.opencomm.model.ConferenceUser;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.TypedValue;

/**
 * @author Lu Yang [frontend]
 */

public class UserView_v2 {

	// Redraws the bitmap and makes the edges circle and adds border color//
	// Borrowed from:
	// http://stackoverflow.com/questions/11012556/border-over-a-bitmap-with-rounded-corners-in-android//
	/**
	 * The context in which the view exists
	 */
	private Context context;
	
	/**
	 * The ConferenceUser object for which the user view exists
	 */
	private ConferenceUser conferenceUser;

	
	private Bitmap getCircleBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// just for conversion
		int borderDips = 6;
		final int borderSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) borderDips, context
				.getResources().getDisplayMetrics());
		// prepare for canvas
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		// prepare canvas for transfer
		paint.setAntiAlias(true);
		paint.setColor(0xFFFFFFFF);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

		// draw bitmap
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		// draw border
		paint.setColor(conferenceUser.getUser().userColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

		return output;
	}
}
