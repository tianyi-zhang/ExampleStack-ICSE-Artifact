public class foo{
    // Borrowed from http://stackoverflow.com/questions/5226922/crop-to-fit-image-in-android
    // Modified to allow for face detection adjustment, startY
    @NonNull private static Bitmap scaleCropToFit(@NonNull Bitmap original, int targetWidth,
                                                  int targetHeight) {
        // Need to scale the image, keeping the aspect ratio first
        int width = original.getWidth();
        int height = original.getHeight();

        float widthScale = (float) targetWidth / (float) width;
        float heightScale = (float) targetHeight / (float) height;
        float scaledWidth;
        float scaledHeight;

        int startX = 0;
        int startY = 0;

        if (widthScale > heightScale) {
            scaledWidth = targetWidth;
            scaledHeight = height * widthScale;
            startY = (int) (scaledHeight - targetHeight) / 2;
            if (startY < 0) {
                startY = 0;
            } else if (startY + targetHeight > scaledHeight) {
                startY = (int)(scaledHeight - targetHeight);
            }
        } else {
            scaledHeight = targetHeight;
            scaledWidth = width * heightScale;
        }

        Bitmap scaledBitmap
                = Bitmap.createScaledBitmap(original, (int) scaledWidth, (int) scaledHeight, true);
        Bitmap bitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
        scaledBitmap.recycle();
        return bitmap;
    }
}