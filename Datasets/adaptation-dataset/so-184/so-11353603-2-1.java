public class foo {
    public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        InternalDimension internalDimension = stringToInternalDimension(dimension);
        final float value = internalDimension.value;
        final float f = TypedValue.applyDimension(internalDimension.unit, value, metrics);
        final int res = (int)(f+0.5f);
        if (res != 0) return res;
        if (value == 0) return 0;
        if (value > 0) return 1;
        return -1;
    }
}