package nodebox.app;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class SwingUtils {

    public static final Color TEXT_SHADOW_COLOR = new Color(255, 255, 255);


    public static void centerOnScreen(Window w) {
        centerOnScreen(w, null);
    }

    public static void centerOnScreen(Window w, Window parent) {
        Rectangle r = new Rectangle();
        if (parent == null) {
            r.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        } else {
            r.setLocation(parent.getLocation());
            r.setSize(parent.getSize());
        }
        // Determine the new location of the alert
        int x = r.x + (r.width - w.getWidth()) / 2;
        int y = r.y + (r.height - w.getHeight()) / 2;
        // Move the alert
        w.setLocation(x, y);
    }

    public static void drawShadowText(Graphics2D g2, String s, int x, int y) {
        drawShadowText(g2, s, x, y, TEXT_SHADOW_COLOR, 1);
    }

    public static void drawShadowText(Graphics2D g2, String s, int x, int y, Color shadowColor, int offset) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Color c = g2.getColor();
        g2.setColor(shadowColor);
        g2.drawString(s, x, y + offset);
        g2.setColor(c);
        g2.drawString(s, x, y);
    }

    public static void drawCenteredShadowText(Graphics2D g2, String s, int x, int y) {
        drawCenteredShadowText(g2, s, x, y, TEXT_SHADOW_COLOR);
    }

    public static void drawCenteredShadowText(Graphics2D g2, String s, int x, int y, Color shadowColor) {
        FontRenderContext frc = g2.getFontRenderContext();
        Rectangle2D bounds = g2.getFont().getStringBounds(s, frc);
        int leftX = (int) (x - (float) bounds.getWidth() / 2);
        drawShadowText(g2, s, leftX, y, shadowColor, 1);
    }

    /**
     * Make the color brighter by the specified factor.
     * <p/>
     * This code is adapted from java.awt.Color to take in a factor.
     * Therefore, it inherits its quirks. Most importantly,
     * a smaller factor makes the color more bright.
     *
     * @param c      a color
     * @param factor the smaller the factor, the brighter.
     * @return a brighter color
     */
    private static Color brighter(Color c, double factor) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255));
    }

    /**
     * Gets the extension of a file.
     *
     * @param f the file
     * @return the extension of the file.
     */
    public static String getExtension(File f) {
        return getExtension(f.getName());
    }

    /**
     * Gets the extension of a file.
     *
     * @param fileName the file name
     * @return the extension of the file.
     */
    public static String getExtension(String fileName) {
        String ext = null;
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            ext = fileName.substring(i + 1).toLowerCase();
        }
        return ext;
    }


    public static File showOpenDialog(Frame owner, String pathName, String extensions, String description) {
        return showFileDialog(owner, pathName, extensions, description, FileDialog.LOAD);
    }

    public static File showSaveDialog(Frame owner, String pathName, String extensions, String description) {
        return showFileDialog(owner, pathName, extensions, description, FileDialog.SAVE);
    }

    private static File showFileDialog(Frame owner, String pathName, String extensions, String description, int fileDialogType) {
        FileDialog fileDialog = new FileDialog(owner, pathName, fileDialogType);
        if (pathName == null || pathName.trim().length() == 0) {
            fileDialog.setDirectory(System.getProperty("user.dir"));
        } else {
            fileDialog.setFile(System.getProperty("user.dir") + pathName);
        }
        fileDialog.setFilenameFilter(new FileExtensionFilter(extensions, description));
        fileDialog.setVisible(true);
        String chosenFile = fileDialog.getFile();
        String dir = fileDialog.getDirectory();
        if (chosenFile != null) {
            return new File(dir + chosenFile);
        } else {
            return null;
        }

    }

    public static String[] parseExtensions(String extensions) {
        StringTokenizer st = new StringTokenizer(extensions, ",");
        String[] ext = new String[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens()) {
            ext[i++] = st.nextToken();
        }
        return ext;
    }

    public static class FileExtensionFilter extends javax.swing.filechooser.FileFilter implements FilenameFilter {
        String[] extensions;
        String desc;

        public FileExtensionFilter(String extensions, String desc) {
            this.extensions = parseExtensions(extensions);
            this.desc = desc;
        }

        public boolean accept(File f) {
            return f.isDirectory() || accept(null, f.getName());
        }

        public boolean accept(File f, String s) {
            String extension = getExtension(s);
            if (extension != null) {
                for (String extension1 : extensions) {
                    if (extension1.equals("*") || extension1.equalsIgnoreCase(extension)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public String getDescription() {
            return desc;
        }
    }

    /**
     * Returns the path of one File relative to another.
     * <p/>
     * From http://stackoverflow.com/questions/204784
     *
     * @param target the target directory
     * @param base   the base directory
     * @return target's path relative to the base directory
     */
    public static String getRelativePath(File target, File base) {
        String[] baseComponents;
        String[] targetComponents;
        try {
            baseComponents = base.getCanonicalPath().split(Pattern.quote(File.separator));
            targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));
        } catch (IOException e) {
            return target.getAbsolutePath();
        }

        // skip common components
        int index = 0;
        for (; index < targetComponents.length && index < baseComponents.length; ++index) {
            if (!targetComponents[index].equals(baseComponents[index]))
                break;
        }

        StringBuilder result = new StringBuilder();
        if (index != baseComponents.length) {
            // backtrack to base directory
            for (int i = index; i < baseComponents.length; ++i)
                result.append("..").append(File.separator);
        }
        for (; index < targetComponents.length; ++index)
            result.append(targetComponents[index]).append(File.separator);
        if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
            // remove final path separator
            result.delete(result.length() - "/".length(), result.length());
        }
        return result.toString();
    }

}
