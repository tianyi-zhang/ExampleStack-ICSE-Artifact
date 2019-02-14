public class foo{
	@Override
	protected void processMouseEvent(MouseEvent event, JLayer<? extends JTabbedPane> jl) {
		if (event.getID() == MouseEvent.MOUSE_CLICKED) {
			pt.setLocation(event.getPoint());
			JTabbedPane tabbedPane = (JTabbedPane) jl.getView();
			int index = tabbedPane.indexAtLocation(pt.x, pt.y);
			if (index >= 0) {
				Rectangle rect = tabbedPane.getBoundsAt(index);
				Dimension d0 = button.getPreferredSize();
				int x0 = rect.x + rect.width - d0.width - 2;
				int y0 = rect.y + (rect.height - d0.height) / 2;
				Rectangle r0 = new Rectangle(x0, y0, d0.width, d0.height);
				if (r0.contains(pt)) {
					tabbedPane.removeTabAt(index);
				}
			}
			jl.getView().repaint();
		}
	}
}