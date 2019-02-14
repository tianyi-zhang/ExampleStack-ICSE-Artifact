public class foo{
  /**
   * Attempts to list all the classes in the specified package as determined by
   * the context class loader
   * 
   * @param pckgname
   *          the package name to search
   * @return a list of classes that exist within that package
   * @throws ClassNotFoundException
   *           if something went wrong
   * 
   *           Ref:
   *           http://stackoverflow.com/questions/1498122/java-loop-on-all-the-
   *           classes-in-the-classpath
   */
  private static List<Class<?>> getClassesForPackage(String pckgname) throws ClassNotFoundException {
    // This will hold a list of directories matching the pckgname. There may be
    // more than one if a package is split over multiple jars/paths
    ArrayList<File> directories = new ArrayList<File>();
    try {
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      if (cld == null) {
        throw new ClassNotFoundException("Can't get class loader.");
      }
      String path = pckgname.replace('.', '/');
      // Ask for all resources for the path
      Enumeration<URL> resources = cld.getResources(path);
      while (resources.hasMoreElements()) {
        directories.add(new File(URLDecoder.decode(resources.nextElement().getPath(), "UTF-8")));
      }
    } catch (NullPointerException x) {
      throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Null pointer exception)");
    } catch (UnsupportedEncodingException encex) {
      throw new ClassNotFoundException(pckgname + " does not appear to be a valid package (Unsupported encoding)");
    } catch (IOException ioex) {
      throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + pckgname);
    }

    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    // For every directory identified capture all the .class files
    for (File directory : directories) {
      if (directory.exists()) {
        // Get the list of the files contained in the package
        String[] files = directory.list();
        for (String file : files) {
          // we are only interested in .class files
          if (file.endsWith(".class")) {
            // removes the .class extension
            try {
              classes.add(Class.forName(pckgname + '.' + file.substring(0, file.length() - 6)));
            } catch (NoClassDefFoundError e) {
              // do nothing. this class hasn't been found by the loader, and we
              // don't care.
            }
          }
        }
      } else {
        throw new ClassNotFoundException(pckgname + " (" + directory.getPath() + ") does not appear to be a valid package");
      }
    }
    return classes;
  }
}