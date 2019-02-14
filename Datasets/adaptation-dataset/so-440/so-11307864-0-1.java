public class foo {
public static String rfc5987_encode(final String s) throws UnsupportedEncodingException {
    final byte[] s_bytes = s.getBytes("UTF-8");
    final int len = s_bytes.length;
    final StringBuilder sb = new StringBuilder(len << 1);
    final char[] digits = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    final byte[] attr_char = {'!','#','$','&','+','-','.','0','1','2','3','4','5','6','7','8','9',           'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','^','_','`',                        'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','|', '~'};
    for (int i = 0; i < len; ++i) {
        final byte b = s_bytes[i];
        if (Arrays.binarySearch(attr_char, b) >= 0)
            sb.append((char) b);
        else {
            sb.append('%');
            sb.append(digits[0x0f & (b >>> 4)]);
            sb.append(digits[b & 0x0f]);
        }
    }

    return sb.toString();
}
}