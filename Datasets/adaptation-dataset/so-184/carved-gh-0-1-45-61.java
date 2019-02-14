public class foo{
    public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        // -- Mimics TypedValue.complexToDimensionPixelSize(int data, DisplayMetrics metrics).
        final float f;
        if (cached.containsKey(dimension)) {
            f = cached.get(dimension);
        } else {
            InternalDimension internalDimension = stringToInternalDimension(dimension);
            final float value = internalDimension.value;
            f = TypedValue.applyDimension(internalDimension.unit, value, metrics);
            cached.put(dimension, f);
        }
        final int res = (int)(f+0.5f);
        if (res != 0) return res;
        if (f == 0) return 0;
        if (f > 0) return 1;
        return -1;
    }
}