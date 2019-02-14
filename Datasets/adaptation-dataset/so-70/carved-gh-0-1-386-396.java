public class foo{
		@Override
		public void paintThumb(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Rectangle t = thumbRect;
			g2d.setColor(Color.black);
			int tw2 = t.width / 2;
			g2d.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
			g2d.drawLine(t.x, t.y, t.x + tw2, t.y + t.height);
			g2d.drawLine(t.x + t.width - 1, t.y, t.x + tw2, t.y + t.height);
		}
}