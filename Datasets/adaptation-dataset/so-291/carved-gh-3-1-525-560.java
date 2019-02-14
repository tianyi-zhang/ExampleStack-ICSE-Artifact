public class foo{
  private static void zipSubDirectory(Context ctxt,
                                      String basePath, File dir,
                                      ZipOutputStream zout)
    throws IOException {
    byte[] buffer = new byte[4096];
    File[] files = dir.listFiles();

    for (File file : files) {
      if (file.isDirectory()) {
        String path = basePath + file.getName() + "/";
        zout.putNextEntry(new ZipEntry(path));
        zipSubDirectory(ctxt, path, file, zout);
        zout.closeEntry();
      }
      else {
        MediaScannerConnection.scanFile(
          ctxt,
          new String[]{file.getAbsolutePath()},
          null,
          null);

        FileInputStream fin = new FileInputStream(file);

        zout.putNextEntry(new ZipEntry(basePath + file.getName()));

        int length;

        while ((length = fin.read(buffer)) > 0) {
          zout.write(buffer, 0, length);
        }

        zout.closeEntry();
        fin.close();
      }
    }
  }
}