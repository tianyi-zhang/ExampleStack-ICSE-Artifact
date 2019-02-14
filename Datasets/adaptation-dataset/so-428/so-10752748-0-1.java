public class foo {
  @Override public void paintIcon(Component c, Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D)g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
    g2.translate(x, y);

    g2.setStroke(new BasicStroke(a));
    g2.setPaint(linec);
    g2.draw(new RoundRectangle2D.Float(a,a,size-2*a-1,size-2*a-1,r,r));

    g2.setStroke(new BasicStroke(b));
    g2.setColor(UIManager.getColor("Panel.background"));
    g2.drawLine(1*f,0*f,1*f,4*f);
    g2.drawLine(2*f,0*f,2*f,4*f);
    g2.drawLine(3*f,0*f,3*f,4*f);
    g2.drawLine(0*f,1*f,4*f,1*f);
    g2.drawLine(0*f,2*f,4*f,2*f);
    g2.drawLine(0*f,3*f,4*f,3*f);

    g2.setPaint(linec);
    Rectangle2D b = s.getBounds();
    Point2D.Double p = new Point2D.Double(
        b.getX() + b.getWidth()/2d, b.getY() + b.getHeight()/2d);
    AffineTransform toCenterAT = AffineTransform.getTranslateInstance(
        size/2d - p.getX(), size/2d - p.getY());
    g2.fill(toCenterAT.createTransformedShape(s));
    g2.translate(-x,-y);
    g2.dispose();
  }
}