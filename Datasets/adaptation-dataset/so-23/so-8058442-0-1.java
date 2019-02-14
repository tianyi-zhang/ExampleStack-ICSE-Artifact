public class foo {
public void applyGrayscaleMaskToAlpha(BufferedImage image, BufferedImage mask)
{
    int width = image.getWidth();
    int height = image.getHeight();

    int[] imagePixels = image.getRGB(0, 0, width, height, null, 0, width);
    int[] maskPixels = mask.getRGB(0, 0, width, height, null, 0, width);

    for (int i = 0; i < imagePixels.length; i++)
    {
        int color = imagePixels[i] & 0x00ffffff; // Mask preexisting alpha
        int alpha = maskPixels[i] << 24; // Shift blue to alpha
        imagePixels[i] = color | alpha;
    }

    image.setRGB(0, 0, width, height, imagePixels, 0, width);
}
}