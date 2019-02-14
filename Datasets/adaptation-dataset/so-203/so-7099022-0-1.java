public class foo {
public void foo(){
BufferedImage image = new BufferedImage(144, 32, BufferedImage.TYPE_INT_RGB);
Graphics g = image.getGraphics();
g.setFont(new Font("Dialog", Font.PLAIN, 24));
Graphics2D graphics = (Graphics2D) g;
graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
graphics.drawString("Hello World!", 6, 24);
ImageIO.write(image, "png", new File("text.png"));

for (int y = 0; y < 32; y++) {
    StringBuilder sb = new StringBuilder();
    for (int x = 0; x < 144; x++)
        sb.append(image.getRGB(x, y) == -16777216 ? " " : image.getRGB(x, y) == -1 ? "#" : "*");
    if (sb.toString().trim().isEmpty()) continue;
    System.out.println(sb);
}
}
}