public class foo{
	public void show(Component invoker, int x, int y) {
		JScrollBar scrollBar = getScrollBar();
		if (scrollBar.isVisible()) {
			int extent = 0;
			int max = 0;
			int i = 0;
			int unit = -1;
			int width = 0;
			for (Component comp : getComponents()) {
				if (!(comp instanceof JScrollBar)) {
					Dimension preferredSize = comp.getPreferredSize();
					width = Math.max(width, preferredSize.width);
					if (unit < 0) {
						unit = preferredSize.height;
					}
					if (i++ < maximumVisibleRows) {
						extent += preferredSize.height;
					}
					max += preferredSize.height;
				}
			}

			Insets insets = getInsets();
			int widthMargin = insets.left + insets.right;
			int heightMargin = insets.top + insets.bottom;
			scrollBar.setUnitIncrement(unit);
			scrollBar.setBlockIncrement(extent);
			scrollBar.setValues(0, heightMargin + extent, 0, heightMargin + max);

			width += scrollBar.getPreferredSize().width + widthMargin;
			int height = heightMargin + extent;

			scrollBar.setSize(new Dimension(width, height));
			setPopupSize(new Dimension(width, height));
		}

		super.show(invoker, x, y);
	}
}