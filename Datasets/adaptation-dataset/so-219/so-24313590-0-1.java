public class foo {
public static Bitmap makeAlpha(Bitmap bit) {
    int width =  bit.getWidth();
    int height = bit.getHeight();
    Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    int [] allpixels = new int [ myBitmap.getHeight()*myBitmap.getWidth()];
    bit.getPixels(allpixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(),myBitmap.getHeight());
    myBitmap.setPixels(allpixels, 0, width, 0, 0, width, height);
            return myBitmap;
      }
}