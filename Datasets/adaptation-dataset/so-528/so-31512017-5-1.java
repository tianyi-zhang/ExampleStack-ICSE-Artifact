public class foo {
        private Bitmap generateBitmap(String appPackageName, Bitmap defaultBitmap)
        {
            // the key for the cache is the icon pack package name and the app package name
            String key = packageName + ":" + appPackageName;

            // if generated bitmaps cache already contains the package name return it
//            Bitmap cachedBitmap = BitmapCache.getInstance(mContext).getBitmap(key);
//            if (cachedBitmap != null)
//                return cachedBitmap;

            // if no support images in the icon pack return the bitmap itself
            if (mBackImages.size() == 0)
                return defaultBitmap;

            Random r = new Random();
            int backImageInd = r.nextInt(mBackImages.size());
            Bitmap backImage = mBackImages.get(backImageInd);
            int w = backImage.getWidth();
            int h = backImage.getHeight();

            // create a bitmap for the result
            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(result);

            // draw the background first
            mCanvas.drawBitmap(backImage, 0, 0, null);

            // create a mutable mask bitmap with the same mask
            Bitmap scaledBitmap = defaultBitmap;
            if (defaultBitmap != null && (defaultBitmap.getWidth() > w || defaultBitmap.getHeight()> h))
                Bitmap.createScaledBitmap(defaultBitmap, (int)(w * mFactor), (int)(h * mFactor), false);

            if (mMaskImage != null)
            {
                // draw the scaled bitmap with mask
                Bitmap mutableMask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas maskCanvas = new Canvas(mutableMask);
                maskCanvas.drawBitmap(mMaskImage,0, 0, new Paint());

                // paint the bitmap with mask into the result
                Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth())/2, (h - scaledBitmap.getHeight())/2, null);
                mCanvas.drawBitmap(mutableMask, 0, 0, paint);
                paint.setXfermode(null);
            }
            else // draw the scaled bitmap without mask
            {
                mCanvas.drawBitmap(scaledBitmap, (w - scaledBitmap.getWidth())/2, (h - scaledBitmap.getHeight())/2, null);
            }

            // paint the front
            if (mFrontImage != null)
            {
                mCanvas.drawBitmap(mFrontImage, 0, 0, null);
            }

            // store the bitmap in cache
//            BitmapCache.getInstance(mContext).putBitmap(key, result);

            // return it
            return result;
        }
}