public class foo{
		@Override
		public void layoutContainer(Container parent) {
			Insets insets = parent.getInsets();

			int width = parent.getWidth() - insets.left - insets.right;
			int height = parent.getHeight() - insets.top - insets.bottom;

			int x = insets.left;
			int y = insets.top;
			int position = 0;

			for (Component comp : parent.getComponents()) {
				if ((comp instanceof JScrollBar) && comp.isVisible()) {
					JScrollBar scrollBar = (JScrollBar) comp;
					Dimension dim = scrollBar.getPreferredSize();
					scrollBar.setBounds(x + width - dim.width - 4, y, dim.width,
							height);
					width -= dim.width;
					position = scrollBar.getValue();
				}
			}

			y -= position;
			Component pre = null;
			int l = 0;
			for (Component comp : parent.getComponents()) {
				++l;
				if (!(comp instanceof JScrollBar) && comp.isVisible()) {
					Dimension pref = comp.getPreferredSize();
					if (l % 2 == 0 && pre != null) {
						comp.setBounds(x, y, width - C2WIDTH, pref.height);
					} else {
						comp.setBounds(x + width - pref.width - 12, y, pref.width, pref.height);
						y += pref.height + 2;
					}
				}
				pre = comp;
			}
		}
}