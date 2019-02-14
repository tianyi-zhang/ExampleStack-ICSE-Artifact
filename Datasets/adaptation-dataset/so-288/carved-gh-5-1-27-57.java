public class foo{
    /**
     * Creates a zip archive
     * http://stackoverflow.com/questions/6683600/zip-compress-a-folder-full-of-files-on-android
     * @param sourcePath
     * @param destPath
     * @throws java.io.IOException
     */
    public static void zip(String sourcePath, String destPath) throws IOException {
        final int BUFFER = 2048;
        File sourceFile = new File(sourcePath);
        BufferedInputStream origin = null;
        FileOutputStream dest = new FileOutputStream(destPath);
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
        if (sourceFile.isDirectory()) {
            // TRICKY: we add 1 to the base path length to exclude the leading path separator
            zipSubFolder(out, sourceFile, sourceFile.getParent().length() + 1);
        } else {
            byte data[] = new byte[BUFFER];
            FileInputStream fi = new FileInputStream(sourcePath);
            origin = new BufferedInputStream(fi, BUFFER);
            String[] segments = sourcePath.split("/");
            String lastPathComponent = segments[segments.length - 1];
            ZipEntry entry = new ZipEntry(lastPathComponent);
            out.putNextEntry(entry);
            int count;
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
        }
        out.close();
    }
}