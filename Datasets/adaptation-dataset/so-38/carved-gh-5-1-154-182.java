public class foo{
		@Override
		public Dimension preferredLayoutSize(Container parent) {
			int visibleAmount = Integer.MAX_VALUE;
			Dimension dim = new Dimension();
			int y = 0;
			int sbw = 0;
			for (Component comp : parent.getComponents()) {
				++y;
				if (comp.isVisible()) {
					if (comp instanceof JScrollBar) {
						JScrollBar scrollBar = (JScrollBar) comp;
						visibleAmount = scrollBar.getVisibleAmount();
						sbw = comp.getPreferredSize().width;
					} else if (y % 2 == 0) {
						Dimension pref = comp.getPreferredSize();
						dim.width = Math.max(dim.width, pref.width + C2WIDTH);
						dim.height += pref.height;
					}
				}
			}

			dim.width += sbw;
			
			Insets insets = parent.getInsets();
			dim.height = Math.min(dim.height + insets.top + insets.bottom,
					visibleAmount);

			return dim;
		}
}