public class foo {
public static Rectangle getMaximumBounds() {
    Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
    DisplayMode mode = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
    Rectangle bounds = new Rectangle();
    bounds.x = insets.left;
    bounds.y = insets.top;
    bounds.width = mode.getWidth() - (insets.left + insets.right);
    bounds.height = mode.getHeight() - (insets.top + insets.bottom);
    return bounds;
  }
}