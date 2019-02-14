package edu.cornell.opencomm.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import edu.cornell.opencomm.interfaces.OCUpdateListener;
import edu.cornell.opencomm.manager.UserManager;
import edu.cornell.opencomm.model.ConferenceUser;
import edu.cornell.opencomm.util.OCBitmapDecoder;

/**
 * @author Spandana Govindgari [frontend], Nora NQ[frontend],Ankit
 *         Singh[frontend
 * 
 *         ISSUES: The border color gets cut off at the top when drawn. However,
 *         on drag the border color is intact. 2. Why does every user have the
 *         same default color? Is hashing working?
 */
public class UserView extends ImageButton implements OCUpdateListener {

	private static String LOG_TAG = UserView.class.getName(); // for error
	// checking
	/**
	 * The context in which the view exists
	 */
	private Context context;
	/**
	 * The ConferenceUser object for which the user view exists
	 */
	private ConferenceUser conferenceUser;

	// private static final int cornerSize = 4;
	// private static final int borderSize = 6;

	public UserView(Context context, ConferenceUser conferenceUser) {
		super(context);
		this.context = context;
		this.conferenceUser = conferenceUser;
		this.conferenceUser.addLocationUpdateListner(this);
		init();

		Log.d(LOG_TAG, "Created a UserView for : "
				+ conferenceUser.getUser().getUsername());
	}

	private void init() {
		if (conferenceUser.getUser().compareTo(UserManager.PRIMARY_USER) == 0) {
			setBackgroundColor(Color.BLACK);
		} else {
			setImageBitmap(getImage());
			
		}
		invalidateLocation();
	}
	private void invalidateLocation(){
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				OCBitmapDecoder.THUMBNAIL_WIDTH,OCBitmapDecoder.THUMBNAIL_HEIGTH+5);
		params.leftMargin = conferenceUser.getX();
		params.topMargin = conferenceUser.getY();
		setLayoutParams(params);
	}
	public Point getCurrentLocation() {
		return conferenceUser.getLocation();
	}

	public ConferenceUser getCUser() {
		return conferenceUser;
	}


	// Redraws the bitmap and makes the edges rounded and adds border color//
	// Borrowed from:
	// http://stackoverflow.com/questions/11012556/border-over-a-bitmap-with-rounded-corners-in-android//

	private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// just for conversion
		// Can this defined else where?
		int borderDips = 6;
		int cornerDips = 4;
		final int borderSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) borderDips, context
				.getResources().getDisplayMetrics());
		final int cornerSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips, context
				.getResources().getDisplayMetrics());
		// prepare for canvas
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		// TODO: Add admin badge here if the user if the moderator
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
		paint.setColor(conferenceUser.getUser().userColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		return output;
	}

	public Bitmap getImage() {
		//Consider caching the image?
		Bitmap  bm = getRoundedCornerBitmap(OCBitmapDecoder.getThumbnailFromResource(
				getResources(), conferenceUser.getUser().getImage()));
		int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) OCBitmapDecoder.THUMBNAIL_WIDTH)/width;
        float scaleHeight = ((float) OCBitmapDecoder.THUMBNAIL_HEIGTH) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
	}
	@Override
	public void invalidate() {
		if(conferenceUser != null){
		invalidateLocation();
		}
		super.invalidate();
	}
	public void onUpdate(int eventId, Object data) {
		invalidate();


	}

}
