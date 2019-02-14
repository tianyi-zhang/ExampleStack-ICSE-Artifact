public class foo {
public static Bitmap rotateBitmap(Bitmap b, float degrees) {
    Matrix m = new Matrix();
    if (degrees != 0) {
        // clockwise
        m.postRotate(degrees, (float) b.getWidth() / 2,
                (float) b.getHeight() / 2);
    }

    try {
        Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                b.getHeight(), m, true);
        if (b != b2) {
            b.recycle();
            b = b2;
        }
    } catch (OutOfMemoryError ex) {
        // We have no memory to rotate. Return the original bitmap.
    }
    return b;
}
}