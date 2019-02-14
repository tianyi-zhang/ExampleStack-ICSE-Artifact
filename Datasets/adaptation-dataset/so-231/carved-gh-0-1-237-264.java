public class foo{
    /**
     * Zips up a sub folder
     * @param out
     * @param folder
     * @param relativePath
     * @throws IOException
     */
    private static void zipSubFolder(ZipOutputStream out, File folder, String relativePath) throws IOException {
        final int BUFFER = 2048;
        File[] fileList = folder.listFiles();
        BufferedInputStream origin = null;
        for (File file : fileList) {
            if (file.isDirectory()) {
                zipSubFolder(out, file, relativePath + "/" + file.getName());
            } else {
                byte data[] = new byte[BUFFER];
                FileInputStream fi = new FileInputStream(file.getPath());
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(relativePath + "/" + file.getName());
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }
        }
    }
}