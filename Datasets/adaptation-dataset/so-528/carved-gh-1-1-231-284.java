public class foo{
    private Bitmap generateBitmap(String componentName, Bitmap defaultBitmap) {
        // If no support images in the icon pack, return the bitmap itself
        if(mBackImages.size() == 0) return defaultBitmap;

        Random r = new Random(generateSeed(componentName));
        int backImageInd = r.nextInt(mBackImages.size());
        Bitmap backImage = mBackImages.get(backImageInd);
        int w = backImage.getWidth();
        int h = backImage.getHeight();

        // Create a bitmap for the result
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(result);

        // Draw the background first
        mCanvas.drawBitmap(backImage, 0, 0, null);

        // Create a mutable mask bitmap with the same mask
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(defaultBitmap, (int) (w * mFactor), (int) (h * mFactor), true);

        if(mMaskImage != null) {
            // Draw the scaled bitmap with mask
            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);
            maskCanvas.drawBitmap(mMaskImage, 0, 0, new Paint());

            // Paint the bitmap with mask into the result
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
            mCanvas.drawBitmap(mutableMask, 0, 0, paint);
            paint.setXfermode(null);
        } else {
            // Draw the scaled bitmap with the back image as mask
            Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas maskCanvas = new Canvas(mutableMask);
            maskCanvas.drawBitmap(backImage, 0, 0, new Paint());

            // Paint the bitmap with mask into the result
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth()) / 2, (h - scaledBitmap.getHeight()) / 2, null);
            mCanvas.drawBitmap(mutableMask, 0, 0, paint);
            paint.setXfermode(null);

        }

        // Paint the front
        if(mFrontImage != null) {
            mCanvas.drawBitmap(mFrontImage, 0, 0, null);
        }

        return result;
    }
}