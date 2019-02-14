public class foo {
public static String toHexString(byte[] bytes) {
    char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    char[] hexChars = new char[bytes.length * 2];
    int v;
    for ( int j = 0; j < bytes.length; j++ ) {
        v = bytes[j] & 0xFF;
        hexChars[j*2] = hexArray[v/16];
        hexChars[j*2 + 1] = hexArray[v%16];
    }
    return new String(hexChars);
}
}