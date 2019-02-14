public class foo{
	@Override
	public void paint(Graphics gr, JComponent jc) {
		super.paint(gr, jc);
		if (jc instanceof JLayer<?>) {
			JLayer<?> jlayer = (JLayer<?>) jc;
			JTabbedPane tabPane =  (JTabbedPane) jlayer.getView();
			for (int i = 0; i < tabPane.getTabCount(); i++) {
				Rectangle rect = tabPane.getBoundsAt(i);
				Dimension dim = button.getPreferredSize();
				int x0 = rect.x + rect.width - dim.width - 2;
				int y0 = rect.y + (rect.height - dim.height) / 2;
				Rectangle r2 = new Rectangle(x0, y0, dim.width, dim.height);
				button.setForeground(r2.contains(pt) ? Color.RED : Color.BLACK);
				SwingUtilities.paintComponent(gr, button, p0, r2);
			}
		}
	}
}