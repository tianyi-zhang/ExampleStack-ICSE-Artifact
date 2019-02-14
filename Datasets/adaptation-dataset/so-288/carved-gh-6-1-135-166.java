public class foo{
    /**
     * Zips up a list of files to output stream
     * @param files
     * @param dest - destination output stream
     */
    public static void zipToStream(File[] files, OutputStream dest) throws IOException {
        final int BUFFER = 2048;
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                dest));

        for(File f:files) {
            if (f.isDirectory()) {
                // TRICKY: we add 1 to the base path length to exclude the leading path separator
                zipSubFolder(out, f, f.getParent().length() + 1);
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(f);
                origin = new BufferedInputStream(fi, BUFFER);
                String[] segments = f.getAbsolutePath().split("/");
                String lastPathComponent = segments[segments.length - 1];
                ZipEntry entry = new ZipEntry(lastPathComponent);
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
            }
        }

        out.close();
    }
}