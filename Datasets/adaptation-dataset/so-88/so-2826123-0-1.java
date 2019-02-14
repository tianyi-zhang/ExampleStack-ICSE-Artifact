public class foo {
private static void copySrcIntoDstAt(final BufferedImage src,
        final BufferedImage dst, final int dx, final int dy) {
    int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
    int[] dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
    int width = src.getWidth();
    int height = src.getHeight();
    int dstoffs = dx + dy * dst.getWidth();
    int srcoffs = 0;
    for (int y = 0 ; y < height ; y++ , dstoffs+= dst.getWidth(), srcoffs += width ) {
        System.arraycopy(srcbuf, srcoffs , dstbuf, dstoffs, width);
    }
}
}