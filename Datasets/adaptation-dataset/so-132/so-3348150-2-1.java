public class foo {
  public static boolean copyJarResourcesRecursively(final File destDir,
      final JarURLConnection jarConnection) throws IOException {

    final JarFile jarFile = jarConnection.getJarFile();

    for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
      final JarEntry entry = e.nextElement();
      if (entry.getName().startsWith(jarConnection.getEntryName())) {
        final String filename = StringUtils.removeStart(entry.getName(), //
            jarConnection.getEntryName());

        final File f = new File(destDir, filename);
        if (!entry.isDirectory()) {
          final InputStream entryInputStream = jarFile.getInputStream(entry);
          if(!FileUtils.copyStream(entryInputStream, f)){
            return false;
          }
          entryInputStream.close();
        } else {
          if (!FileUtils.ensureDirectoryExists(f)) {
            throw new IOException("Could not create directory: "
                + f.getAbsolutePath());
          }
        }
      }
    }
    return true;
  }
}