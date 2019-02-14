public class foo {
public static Bitmap scaleCropToFit(Bitmap original, int targetWidth, int targetHeight){
    //Need to scale the image, keeping the aspect ration first
    int width = original.getWidth();
    int height = original.getHeight();

    float widthScale = (float) targetWidth / (float) width;
    float heightScale = (float) targetHeight / (float) height;
    float scaledWidth;
    float scaledHeight;

    int startY = 0;
    int startX = 0;

    if (widthScale > heightScale) {
        scaledWidth = targetWidth;
        scaledHeight = height * widthScale;
        //crop height by...
        startY = (int) ((scaledHeight - targetHeight) / 2);
    } else {
        scaledHeight = targetHeight;
        scaledWidth = width * heightScale;
        //crop width by..
        startX = (int) ((scaledWidth - targetWidth) / 2);
    }

    Bitmap scaledBitmap = Bitmap.createScaledBitmap(original, (int) scaledWidth, (int) scaledHeight, true);

    Bitmap resizedBitmap = Bitmap.createBitmap(scaledBitmap, startX, startY, targetWidth, targetHeight);
    return resizedBitmap;
}
}