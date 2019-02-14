public class foo{
  // A slightly modified version of answer given here:
  // http://stackoverflow.com/a/453067/1059744
  private static int countNewLines(String filename) throws IOException {
    InputStream is = new BufferedInputStream(new FileInputStream(filename));
    byte[] c = new byte[1024];
    int count = 0;
    int readChars = 0;
    boolean empty = true;

    while ((readChars = is.read(c)) != -1) {
      empty = false;
      for (int i = 0; i < readChars; ++i) {
        if (c[i] == '\n') {
          ++count;
        }
      }
    }

    is.close();
    return (count == 0 && !empty) ? 1 : count;
  }
}