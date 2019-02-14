public class foo{
            public void mouseDragged(MouseEvent e) {

                currentMouseLocation = e.getPoint();
                if (!dragging) {
                    // Gets the tab index based on the mouse position
                    int tabNumber = getUI().tabForCoordinate(DraggableTabbedPane.this, e.getX(), e.getY());
                    if (tabNumber == 0 || tabNumber == getTabCount() - 1) return;
                    if (tabNumber > 0) {
                        draggedTabIndex = tabNumber;
                        Rectangle bounds = getUI().getTabBounds(DraggableTabbedPane.this, tabNumber);

                        // Paint the tabbed pane to a buffer
                        Image totalImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics totalGraphics = totalImage.getGraphics();
                        totalGraphics.setClip(bounds);
                        // Don't be double buffered when painting to a static image.
                        setDoubleBuffered(false);
                        paintComponent(totalGraphics);

                        // Paint just the dragged tab to the buffer
                        tabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics graphics = tabImage.getGraphics();
                        graphics.drawImage(totalImage, 0, 0, bounds.width, bounds.height, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, DraggableTabbedPane.this);
                        dragging = true;
                        repaint();
                    }
                } else {
                    TabType tt = getTabType(e);
                    if (tt.getType() != TabTypeEnum.TAB_NEITHER) {
                        toPlace = tt;
                    } else {
                        toPlace = null;
                    }
                    // Need to repaint
                    repaint();
                }

                super.mouseDragged(e);
            }
}