public class foo{
    // Ref: http://stackoverflow.com/questions/221830/set-bufferedimage-alpha-mask-in-java/8058442#8058442
    public void storeGreyscaleMapIntoAlpha(BufferedImage imageWithoutAlpha, BufferedImage greyscaleImage) {
        int width = imageWithoutAlpha.getWidth();
        int height = imageWithoutAlpha.getHeight();

        int[] imagePixels = imageWithoutAlpha.getRGB(0, 0, width, height, null, 0, width);
        int[] maskPixels = greyscaleImage.getRGB(0, 0, width, height, null, 0, width);

        for (int i = 0; i < imagePixels.length; i++) {
            int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
            int alpha = maskPixels[i] << 24; // Shift blue to alpha
            imagePixels[i] = color | alpha;
        }

        imageWithoutAlpha.setRGB(0, 0, width, height, imagePixels, 0, width);
    }
}