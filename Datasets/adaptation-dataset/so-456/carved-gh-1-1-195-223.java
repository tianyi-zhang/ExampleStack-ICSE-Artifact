public class foo{
  public static void uncompressTarGZ(File tarFile, File dest) throws IOException {
    // http://stackoverflow.com/questions/11431143/how-to-untar-a-tar-file-using-apache-commons/14211580#14211580
    dest.mkdir();
    TarArchiveInputStream tarIn = null;
    tarIn = new TarArchiveInputStream(new GzipCompressorInputStream(new BufferedInputStream(
            new FileInputStream(tarFile))));
    TarArchiveEntry tarEntry = tarIn.getNextTarEntry();
    while (tarEntry != null) {// create a file with the same name as the tarEntry
      File destPath = new File(dest, tarEntry.getName());
      if (destPath.getName().equals("cassandra")){
        destPath.setExecutable(true);
      }
      if (tarEntry.isDirectory()) {
        destPath.mkdirs();
      } else {
        destPath.createNewFile();
        byte[] btoRead = new byte[1024];
        BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(destPath));
        int len = 0;
        while ((len = tarIn.read(btoRead)) != -1) {
          bout.write(btoRead, 0, len);
        }
        bout.close();
        btoRead = null;
      }
      tarEntry = tarIn.getNextTarEntry();
    }
    tarIn.close();
  }
}