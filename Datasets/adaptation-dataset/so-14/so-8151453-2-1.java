public class foo {
  @Override
  public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
    boolean didResize = (f.getWidth() != newWidth || f.getHeight() != newHeight);
    if (!inBounds((JInternalFrame) f, newX, newY, newWidth, newHeight)) {
      Container parent = f.getParent();
      Dimension parentSize = parent.getSize();
      int boundedX = (int) Math.min(Math.max(0, newX), parentSize.getWidth() - newWidth);
      int boundedY = (int) Math.min(Math.max(0, newY), parentSize.getHeight() - newHeight);
      f.setBounds(boundedX, boundedY, newWidth, newHeight);
    } else {
      f.setBounds(newX, newY, newWidth, newHeight);
    }
    if(didResize) {
      f.validate();
    }
  }
}