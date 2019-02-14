public class foo{
    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            Log.d("reqHeight", ""+reqHeight);
            Log.d("reqWidth", ""+reqWidth);
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = (heightRatio < widthRatio ? heightRatio : widthRatio);
            Log.d("inSampleSize", ""+inSampleSize);
            // We round the value to the highest, always.
            if ((height / inSampleSize) > reqHeight || (width / inSampleSize > reqWidth)) {
                inSampleSize++;
            }

        }

        return inSampleSize;
    }
}