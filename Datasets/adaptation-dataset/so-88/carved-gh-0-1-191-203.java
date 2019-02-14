public class foo{
  // http://stackoverflow.com/questions/2825837/java-how-to-do-fast-copy-of-a-bufferedimages-pixels-unit-test-included/2826123#2826123
  private static void copySrcIntoDstAt(final BufferedImage src,
                                       final BufferedImage dst, final int dx, final int dy) {
    int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
    int[] dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
    int width  = src.getWidth();
    int height = src.getHeight();
    int srcoffs = 0;
    int dstoffs = dx + dy * dst.getWidth();
    for (int y = 0 ; y < height ; y++ , dstoffs += dst.getWidth(), srcoffs += width ) {
      System.arraycopy(srcbuf, srcoffs , dstbuf, dstoffs, width);
    }
  }
}