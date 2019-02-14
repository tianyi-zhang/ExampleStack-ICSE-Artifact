public class foo{
	/**
	 * Scale image by given ratio.
	 * Algorithm found at
	 * http://stackoverflow.com/questions/1069095/how-do-you-create-a-thumbnail-image-out-of-a-jpeg-in-java
	 * @param source
	 * @param ratio
	 * @return Scaled image
	 */
	private static BufferedImage scale(BufferedImage source, double ratio) {
	  int w = (int) (source.getWidth() * ratio);
	  int h = (int) (source.getHeight() * ratio);
	  BufferedImage bi = getCompatibleImage(w, h);
	  Graphics2D g2d = bi.createGraphics();
	  double xScale = (double) w / source.getWidth();
	  double yScale = (double) h / source.getHeight();
	  AffineTransform at = AffineTransform.getScaleInstance(xScale,yScale);
	  g2d.drawRenderedImage(source, at);
	  g2d.dispose();
	  return bi;
	}
}