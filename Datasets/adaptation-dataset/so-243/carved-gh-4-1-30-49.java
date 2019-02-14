public class foo{
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (getText().length() == 0) {
			g.setFont(_font);

			int adjust_y = _font == ElephantWindow.fontMediumPlus ? 2 + adjustOS : 0;

			int h = getHeight();
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			Insets ins = getInsets();
			FontMetrics fm = g.getFontMetrics();
			int c0 = getBackground().getRGB();
			int c1 = getForeground().getRGB();
			int m = 0xfefefefe;
			int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
			g.setColor(new Color(c2, true));
			g.drawString(_hint, ins.left, h / 2 + fm.getAscent() / 2 - 2 + adjust_y);
		}
	}
}