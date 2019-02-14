public class foo {
  private static boolean copyFilesRecusively(final File toCopy,
      final File destDir) {
    assert destDir.isDirectory();

    if (!toCopy.isDirectory()) {
      return FileUtils.copyFile(toCopy, new File(destDir, toCopy.getName()));
    } else {
      final File newDestDir = new File(destDir, toCopy.getName());
      if (!newDestDir.exists() && !newDestDir.mkdir()) {
        return false;
      }
      for (final File child : toCopy.listFiles()) {
        if (!FileUtils.copyFilesRecusively(child, newDestDir)) {
          return false;
        }
      }
    }
    return true;
  }
}