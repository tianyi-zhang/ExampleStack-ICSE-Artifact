package no.raiom.utils;

public class ByteUtils {

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String toByteString(byte[] bytes) {
        StringBuilder byteString = new StringBuilder();
        for (byte b : bytes) {
            if (b >= 32 && b <= 127) {
                byteString.append((char) b);
            } else {
                byteString.append(String.format("\\x%02x", b));
            }
        }
        return byteString.toString();
    }

    public static String toByteString(byte[] bytes, int start, int length) {
        StringBuilder byteString = new StringBuilder();
        int maxlength = bytes.length < start + length ? bytes.length : start + length;
        for (int i = start; i < maxlength; i++) {
            if (bytes[i] >= 32 && bytes[i] <= 127) {
                byteString.append((char) bytes[i]);
            } else {
                byteString.append(String.format("\\x%02x", bytes[i]));
            }
        }
        return byteString.toString();
    }

    public static int toUInt16(byte[] bytes, int start) {
        return (bytes[start] & 0xff) + ((bytes[start + 1] & 0xff) << 8);
    }
}