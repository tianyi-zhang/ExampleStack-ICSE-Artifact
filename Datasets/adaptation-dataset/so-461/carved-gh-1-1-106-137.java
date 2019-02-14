public class foo{
    // Helper method to compress from ZIP
    // Inspired from: 
    // http://stackoverflow.com/questions/1399126/java-util-zip-recreating-directory-structure
    // directory: the directoyr to compress
    // prefixDir: The prefix in ziped file for compressed dir
    // zout: the zip output stream opened in the caller
    private static void zipDirectory(File directory, String prefixDir, ZipOutputStream zout) throws IOException {
        URI base = directory.toURI();
        LinkedList<File> queue = new LinkedList<File>();
        queue.addLast(directory);
        while (!queue.isEmpty()) {
            directory = queue.removeFirst();
            for (File kid : directory.listFiles()) {
                String name = prefixDir + base.relativize(kid.toURI()).getPath();
                if (kid.isDirectory()) {
                    queue.addLast(kid);
                    name = name.endsWith("/") ? name : name + "/";
                    zout.putNextEntry(new ZipEntry(name));
                } else {
                    zout.putNextEntry(new ZipEntry(name));
                    InputStream in = new FileInputStream(kid);
                    try {
                        IOUtils.copy(in , zout);
                    } finally {
                        in.close();
                    }

                    zout.closeEntry();
                }
            }
        }
    }
}