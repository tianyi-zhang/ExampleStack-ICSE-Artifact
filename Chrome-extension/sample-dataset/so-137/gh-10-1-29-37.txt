package usr.pashik.securd.crypto;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.Arrays;

/**
 * Created by pashik on 17.03.14 21:05.
 */
public class BinaryService {
    final protected static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    final protected static BASE64Encoder BASE_64_ENCODER = new BASE64Encoder();
    final protected static BASE64Decoder BASE_64_DECODER = new BASE64Decoder();

    public static byte[] mergeArrays(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] result = new byte[aLen + bLen];
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
        return result;
    }

    public static byte[] truncate(byte[] value, int newLength) {
        return Arrays.copyOf(value, newLength);
    }

    public static String bytes2hex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String bytes2base64(byte[] bytes) {
        return BASE_64_ENCODER.encode(bytes).replace("\r\n", "");
    }

    public static byte[] base642bytes(String base64) {
        try {
            return BASE_64_DECODER.decodeBuffer(base64);
        } catch (Exception e) {
            return null;
        }
    }
}
