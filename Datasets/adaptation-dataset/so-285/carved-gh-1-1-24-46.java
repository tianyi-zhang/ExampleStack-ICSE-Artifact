public class foo{
    public static BufferedImage stringToBufferedImage(Font font, String s) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = img.getGraphics();
        g.setFont(font);

        FontRenderContext frc = g.getFontMetrics().getFontRenderContext();
        Rectangle2D rect = font.getStringBounds(s, frc);
        g.dispose();

        img = new BufferedImage((int) Math.ceil(rect.getWidth()), (int) Math.ceil(rect.getHeight()), BufferedImage.TYPE_4BYTE_ABGR);
        g = img.getGraphics();
        g.setColor(Color.black);
        g.setFont(font);

        FontMetrics fm = g.getFontMetrics();
        int x = 0;
        int y = fm.getAscent();

        g.drawString(s, x, y);
        g.dispose();

        return img;
    }
}