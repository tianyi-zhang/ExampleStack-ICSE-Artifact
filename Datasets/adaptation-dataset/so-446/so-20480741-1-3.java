public class foo {
public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

    Matrix matrix = new Matrix();
    switch (orientation) {
        case ExifInterface.ORIENTATION_NORMAL:
            return bitmap;
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
            matrix.setScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_ROTATE_180:
            matrix.setRotate(180);
            break;
        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
            matrix.setRotate(180);
            matrix.postScale(-1, 1);
            break;
        case ExifInterface.ORIENTATION_TRANSPOSE:
            matrix.setRotate(90);
            matrix.postScale(-1, 1);
            break;
       case ExifInterface.ORIENTATION_ROTATE_90:
           matrix.setRotate(90);
           break;
       case ExifInterface.ORIENTATION_TRANSVERSE:
           matrix.setRotate(-90);
           matrix.postScale(-1, 1);
           break;
       case ExifInterface.ORIENTATION_ROTATE_270:
           matrix.setRotate(-90);
           break;
       default:
           return bitmap;
    }
    try {
        Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bmRotated;
    }
    catch (OutOfMemoryError e) {
        e.printStackTrace();
        return null;
    }
}
}