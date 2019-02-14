public class foo {
private BufferedImage scale(BufferedImage source,double ratio) {
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