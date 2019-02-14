public class foo {
public static byte [] hexStringToByteArray (final String s) {
    if (s == null || (s.length () % 2) == 1)
      throw new IllegalArgumentException ();
    final char [] chars = s.toCharArray ();
    final int len = chars.length;
    final byte [] data = new byte [len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit (chars[i], 16) << 4) + Character.digit (chars[i + 1], 16));
    }
    return data;
  }
}