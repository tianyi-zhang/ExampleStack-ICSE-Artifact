public class foo{
    /**
     * https://futurestud.io/blog/how-to-blur-images-efficiently-with-androids-renderscript
     * https://developer.xamarin.com/recipes/android/other_ux/drawing/blur_an_image_with_renderscript/
     *
     * @param context
     * @param image      original bitmap
     * @param width      output bitmap width
     * @param height     output bitmap height
     * @param blurRadius blur radius ;  blurRadius in section (0,25]
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(Context context, Bitmap image, int width, int height, int blurRadius) {

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(blurRadius);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        rs.destroy();
        return outputBitmap;
    }
}