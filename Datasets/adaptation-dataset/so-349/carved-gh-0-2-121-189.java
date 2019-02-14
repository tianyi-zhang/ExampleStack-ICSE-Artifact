public class foo{
  @Override
  public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

    // Work out the lowest inside line of the bubble
    int bottomLineY = height - thickness - pointerSize - 1;

    // Draw the rounded bubble border with a few tweaks for text fields and areas
    RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
      strokePad + 2,
      strokePad + 2,
      width - thickness - strokePad - 3,
      bottomLineY - 2,
      radii,
      radii);

    Area area = new Area(bubble);

    // Should the "speech pointer" polygon be included?
    if (pointerSize > 0) {
      Polygon pointer = new Polygon();
      int pointerPad = 4;

      // Place on left
      if (pointerLeft) {
        // Left point
        pointer.addPoint(strokePad + radii + pointerPad, bottomLineY);
        // Right point
        pointer.addPoint(strokePad + radii + pointerPad + pointerSize, bottomLineY);
        // Bottom point
        pointer.addPoint(strokePad + radii + pointerPad + (pointerSize / 2), height - strokePad);
      } else {
        // Left point
        pointer.addPoint(width - (strokePad + radii + pointerPad), bottomLineY);
        // Right point
        pointer.addPoint(width - (strokePad + radii + pointerPad + pointerSize), bottomLineY);
        // Bottom point
        pointer.addPoint(width - (strokePad + radii + pointerPad + (pointerSize / 2)), height - strokePad);
      }
      area.add(new Area(pointer));
    }

    // Get the 2D graphics context
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHints(hints);

    // Paint the background color of the parent everywhere
    // outside the clip of the text bubble
    Component parent = c.getParent();
    if (parent != null) {

      Color bg = parent.getBackground();
      Rectangle rect = new Rectangle(0, 0, width, height);

      Area borderRegion = new Area(rect);
      borderRegion.subtract(area);

      g2.setClip(borderRegion);
      g2.setColor(bg);
      g2.fillRect(0, 0, width, height);
      g2.setClip(null);

    }

    // Set the border color
    g2.setColor(color);
    g2.setStroke(stroke);
    g2.draw(area);

  }
}