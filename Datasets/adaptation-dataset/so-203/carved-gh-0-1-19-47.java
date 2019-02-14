public class foo{
    static String convert(String text) {

        BufferedImage image = new BufferedImage(144, 32, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Dialog", Font.PLAIN, 14));
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.drawString(text, 6, 24);

        StringBuilder rendered = new StringBuilder();
        try {
            ImageIO.write(image, "png", new File("text.png"));

            for (int y = 0; y < 32; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < 144; x++)
                    sb.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
                if (sb.toString().trim().isEmpty()) continue;
                rendered.append(sb);
                rendered.append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rendered.toString();

    }
}