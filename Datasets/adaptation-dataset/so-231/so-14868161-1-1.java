public class foo {
private void zipSubFolder(ZipOutputStream out, File folder,
        int basePathLength) throws IOException {

    final int BUFFER = 2048;

    File[] fileList = folder.listFiles();
    BufferedInputStream origin = null;
    for (File file : fileList) {
        if (file.isDirectory()) {
            zipSubFolder(out, file, basePathLength);
        } else {
            byte data[] = new byte[BUFFER];
            String unmodifiedFilePath = file.getPath();
            String relativePath = unmodifiedFilePath
                    .substring(basePathLength);
            FileInputStream fi = new FileInputStream(unmodifiedFilePath);
            origin = new BufferedInputStream(fi, BUFFER);
            ZipEntry entry = new ZipEntry(relativePath);
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