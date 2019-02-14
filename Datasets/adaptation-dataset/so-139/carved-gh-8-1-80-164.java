public class foo{
  /**
   * taken from stackoverflow:
   * http://stackoverflow.com/questions/204784/how-to-construct
   * -a-relative-path-in-java-from-two-absolute-paths-or-urls
   * 
   * Get the relative path from one file to another, specifying the directory
   * separator. If one of the provided resources does not exist, it is assumed
   * to be a file unless it ends with '/' or '\'.
   * 
   * @param basePath
   *          basePath is calculated from this file
   * @param targetPath
   *          targetPath is calculated to this file
   * @param pathSeparator
   *          directory separator. The platform default is not assumed so that
   *          we can test Unix behaviour when running on Windows (for example)
   * 
   * @return
   */
  public static String getRelativePath(String basePath, String targetPath, String pathSeparator) {

    // Normalize the paths
    String normalizedTargetPath = normalizeNoEndSeparator(targetPath, pathSeparator);
    String normalizedBasePath = normalizeNoEndSeparator(basePath, pathSeparator);
    
    if (normalizedTargetPath==null)
      return "";
    
    if (normalizedBasePath==null)
      return targetPath;

    String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
    String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

    // First get all the common elements. Store them as a string,
    // and also count how many of them there are.
    StringBuilder common = new StringBuilder();

    int commonIndex = 0;
    while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].equals(base[commonIndex])) {
      common.append(target[commonIndex] + pathSeparator);
      commonIndex++;
    }

    if (commonIndex == 0) {
      // No single common path element. This most
      // likely indicates differing drive letters, like C: and D:.
      // These paths cannot be relativized.
      return normalizedTargetPath;
      //      throw new PathResolutionException("No common path element found for '" + normalizedTargetPath + "' and '" + normalizedBasePath + "'");
    }

    // The number of directories we have to backtrack depends on whether the base is a file or a dir
    // For example, the relative path from
    //
    // /foo/bar/baz/gg/ff to /foo/bar/baz
    // 
    // ".." if ff is a file
    // "../.." if ff is a directory
    //
    // The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
    // the resource referred to by this path may not actually exist, but it's the best I can do
    boolean baseIsFile = true;

    File baseResource = new File(normalizedBasePath);

    if (baseResource.exists()) {
      baseIsFile = baseResource.isFile();

    } else if (basePath.endsWith(pathSeparator)) {
      baseIsFile = false;
    }

    StringBuilder relative = new StringBuilder();

    if (base.length != commonIndex) {
      int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;

      for (int i = 0; i < numDirsUp; i++) {
        relative.append(".." + pathSeparator);
      }
    }
    relative.append(safeSubstring(normalizedTargetPath, common.length()));
    return relative.toString();
  }
}