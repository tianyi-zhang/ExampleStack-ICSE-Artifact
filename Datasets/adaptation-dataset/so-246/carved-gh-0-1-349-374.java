public class foo{
	/**
	 * Method to rotate bitmap as found here:
	 * http://stackoverflow.com/questions/13430895/capture-photo-rotate-90-degree-in-samsung-mobile
	 * @param bitmap
	 * @param degrees
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
	    Matrix matrix = new Matrix();
	    if (degrees != 0) {
	        // rotate clockwise
	        matrix.postRotate(degrees, (float) bitmap.getWidth() / 2,
	                (float) bitmap.getHeight() / 2);
	    }
	    try {
	        Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
	                bitmap.getHeight(), matrix, true);
	        if (bitmap != b2) {
	            bitmap.recycle();
	            bitmap = b2;
	        }
	    } catch (OutOfMemoryError ex) {
	        // We have no memory to rotate. Return the original bitmap.
	    }
	    return bitmap;
	}
}