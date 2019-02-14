public class foo{
  /**
   * DOCUMENT ME!
   *
   * @param toCopy DOCUMENT ME!
   * @param destDir DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public static boolean copyFilesRecusively(final File toCopy, final File destDir) {
    assert destDir.isDirectory();

    if (!toCopy.isDirectory()) {
      return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
    } else {
      final File newDestDir = new File(destDir, toCopy.getName());

      if (!newDestDir.exists() && !newDestDir.mkdir()) {
        return false;
      }

      File[] files = toCopy.listFiles();

      for (int i = 0; i < files.length; i++) {
        File child = files[i];

        if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
          return false;
        }
      }
    }

    return true;
  }
}