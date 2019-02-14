public class foo{
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {

        Graphics2D g2 = (Graphics2D) g;

        int bottomLineY = height - thickness - pointerSize;

        RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(0 + strokePad, 0 + strokePad, width - thickness, bottomLineY,
                radii, radii);

        Polygon pointer = new Polygon();

        if (left)
        {
            // left point
            pointer.addPoint(strokePad + radii + pointerPad, bottomLineY);
            // right point
            pointer.addPoint(strokePad + radii + pointerPad + pointerSize, bottomLineY);
            // bottom point
            pointer.addPoint(strokePad + radii + pointerPad + (pointerSize / 2), height - strokePad);
        }
        else
        {
            // left point
            pointer.addPoint(width - (strokePad + radii + pointerPad), bottomLineY);
            // right point
            pointer.addPoint(width - (strokePad + radii + pointerPad + pointerSize), bottomLineY);
            // bottom point
            pointer.addPoint(width - (strokePad + radii + pointerPad + (pointerSize / 2)), height - strokePad);
        }

        Area area = new Area(bubble);
        area.add(new Area(pointer));

        g2.setRenderingHints(hints);

        // Paint the BG color of the parent, everywhere outside the clip
        // of the text bubble.
        Component parent = c.getParent();
        if (parent != null)
        {
            Color bg = parent.getBackground();
            Rectangle rect = new Rectangle(0, 0, width, height);
            Area borderRegion = new Area(rect);
            borderRegion.subtract(area);
            g2.setClip(borderRegion);
            g2.setColor(bg);
            g2.fillRect(0, 0, width, height);
            g2.setClip(null);
        }

        g2.setColor(color);
        g2.setStroke(stroke);
        g2.draw(area);
    }
}