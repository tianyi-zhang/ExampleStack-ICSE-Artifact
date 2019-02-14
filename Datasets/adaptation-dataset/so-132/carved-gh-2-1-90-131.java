public class foo{
  /**
   * DOCUMENT ME!
   *
   * @param destDir DOCUMENT ME!
   * @param jarConnection DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws IOException DOCUMENT ME!
   */
  public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection)
      throws IOException {
    final JarFile jarFile = jarConnection.getJarFile();

    Enumeration e = jarFile.entries();

    while (e.hasMoreElements()) {
      final JarEntry entry = (JarEntry) e.nextElement();

      if (entry.getName().startsWith(jarConnection.getEntryName())) {
        final String filename = entry.getName().replace(jarConnection.getEntryName(), "");

        final File f = new File(destDir, filename);

        if (!entry.isDirectory()) {
          final InputStream entryInputStream = jarFile.getInputStream(entry);

          if (!FileUtils.copyStream(entryInputStream, f)) {
            return false;
          }

          entryInputStream.close();
        } else {
          if (!FileUtils.ensureDirectoryExists(f)) {
            throw new IOException("Could not create directory: " + f.getAbsolutePath());
          }
        }
      }
    }

    return true;
  }
}