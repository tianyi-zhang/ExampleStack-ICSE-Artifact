public class foo{
    private String byteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] hexChars = new char[MD5_STR_LEN];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_MAP[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_MAP[v & 0x0F];
        }
        return new String(hexChars);
    }
}