public class foo{
		@Override
		public Dimension minimumLayoutSize(Container parent) {
			int visibleAmount = Integer.MAX_VALUE;
			Dimension dim = new Dimension();
			Component pre = null;
			int y = 0;
			for (Component comp : parent.getComponents()) {
				++y;
				if (comp.isVisible()) {
					if (comp instanceof JScrollBar) {
						JScrollBar scrollBar = (JScrollBar) comp;
						visibleAmount = scrollBar.getVisibleAmount();
					} else {
						if (y % 2 == 0 && pre != null) {
							Dimension min = comp.getMinimumSize();
							dim.width = Math.max(dim.width, min.width + C2WIDTH);
							dim.height += min.height;
						}
					}
				}
				pre = comp;
			}

			Insets insets = parent.getInsets();
			dim.height = Math.min(dim.height + insets.top + insets.bottom,
					visibleAmount);

			return dim;
		}
}