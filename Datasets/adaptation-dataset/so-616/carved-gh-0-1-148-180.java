public class foo{
    /**
     * decodes YUV to RGB
     * @source https://stackoverflow.com/questions/8350230/android-how-to-display-camera-preview-with-callback
     * @param nv21
     * @param width
     * @param height
     * @return
     */
    private int[] decodeYuvToRgb(byte[] nv21, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize + 1];

        // Convert YUV to RGB
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) nv21[i * width + j]));
                int u = (0xff & ((int) nv21[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) nv21[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }

        return rgba;
    }
}