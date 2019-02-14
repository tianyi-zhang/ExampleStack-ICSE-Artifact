public class foo{
  private static boolean runCommand(String command, String args, String file) {
    System.out.println("Trying to exec:\n   cmd = " + command + "\n   args = " + args + "\n   %s = " + file);
    String[] parts = prepareCommand(command, args, file);
    try {
      Process p = Runtime.getRuntime().exec(parts);
      if (p == null) {
        return false;
      }
      try {
        int retval = p.exitValue();
        if (retval == 0) {
          System.err.println("Process ended immediately.");
          return false;
        } else {
          System.err.println("Process crashed.");
          return false;
        }
      } catch (IllegalThreadStateException itse) {
        System.err.println("Process is running.");
        return true;
      }
    } catch (IOException e) {
      logErr("Error running command.", e);
      return false;
    }
  }
}