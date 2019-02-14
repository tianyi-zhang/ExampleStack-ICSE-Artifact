public class foo {
  @Override public void paint(Graphics g, JComponent c) {
    super.paint(g, c);
    if (c instanceof JLayer) {
      JLayer jlayer = (JLayer) c;
      JTabbedPane tabPane = (JTabbedPane) jlayer.getView();
      for (int i = 0; i < tabPane.getTabCount(); i++) {
        Rectangle rect = tabPane.getBoundsAt(i);
        Dimension d = button.getPreferredSize();
        int x = rect.x + rect.width - d.width - 2;
        int y = rect.y + (rect.height - d.height) / 2;
        Rectangle r = new Rectangle(x, y, d.width, d.height);
        button.setForeground(r.contains(pt) ? Color.RED : Color.BLACK);
        SwingUtilities.paintComponent(g, button, p, r);
      }
    }
  }
}