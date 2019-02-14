public class foo {
  @Override protected void processMouseEvent(MouseEvent e, JLayer<? extends JTabbedPane> l) {
    if (e.getID() == MouseEvent.MOUSE_CLICKED) {
      pt.setLocation(e.getPoint());
      JTabbedPane tabbedPane = (JTabbedPane) l.getView();
      int index = tabbedPane.indexAtLocation(pt.x, pt.y);
      if (index >= 0) {
        Rectangle rect = tabbedPane.getBoundsAt(index);
        Dimension d = button.getPreferredSize();
        int x = rect.x + rect.width - d.width - 2;
        int y = rect.y + (rect.height - d.height) / 2;
        Rectangle r = new Rectangle(x, y, d.width, d.height);
        if (r.contains(pt)) {
          tabbedPane.removeTabAt(index);
        }
      }
      l.getView().repaint();
    }
  }
}