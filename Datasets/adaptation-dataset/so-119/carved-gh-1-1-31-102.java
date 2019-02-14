public class foo{
	public DraggableTabbedPane() {
		super();
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(!dragging) {
					// Gets the tab index based on the mouse position
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());

					if(tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);

						// Paint the tabbed pane to a buffer
						Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);

						// Don't be double buffered when painting to a static image.
						setDoubleBuffered(false);
						paint(totalGraphics);

						// Paint just the dragged tab to the buffer
						tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
						Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y+bounds.height, DraggableTabbedPane.this);

						dragging = true;
						repaint();

						graphics.dispose();
						totalGraphics.dispose();
					}
				}
				else {
					currentMouseLocation = e.getPoint();

					// Need to repaint
					repaint();
				}

				super.mouseDragged(e);
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {

				if(dragging) {
					int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), 10);

					if (e.getX() < 0) {
						tabNumber = 0;
					}
					else if (tabNumber == -1) {
						tabNumber = getTabCount() - 1;
					}

					if (tabNumber >= 0) {
						Component comp = getComponentAt(draggedTabIndex);
						Component title = getTabComponentAt(draggedTabIndex);
						removeTabAt(draggedTabIndex);
						insertTab("", null, comp, null, tabNumber);
						setTabComponentAt(tabNumber, title);
						setSelectedIndex(tabNumber);
					}
				}

				dragging = false;
				tabImage = null;
			}
		});
	}
}