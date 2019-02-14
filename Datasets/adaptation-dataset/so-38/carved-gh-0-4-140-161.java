public class foo{
		@Override public Dimension preferredLayoutSize(Container parent) {
			int visibleAmount = Integer.MAX_VALUE;
			Dimension dim = new Dimension();
			for(Component comp :parent.getComponents()){
				if(comp.isVisible()) {
					if(comp instanceof JScrollBar){
						JScrollBar scrollBar = (JScrollBar) comp;
						visibleAmount = scrollBar.getVisibleAmount();
					}
					else {
						Dimension pref = comp.getPreferredSize();
						dim.width = Math.max(dim.width, pref.width);
						dim.height += pref.height;
					}
				}
			}

			Insets insets = parent.getInsets();
			dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);

			return dim;
		}
}