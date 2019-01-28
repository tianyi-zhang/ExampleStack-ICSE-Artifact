package ch.fhnw.imvs.util;

import java.nio.ByteBuffer;

public class ConverterUtils {
	final protected static char[] HEY_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String toHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEY_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEY_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String toHexString(short number) {
		byte[] bytes = toByteArray(number);
		return toHexString(bytes);
	}

	public static byte[] toByteArray(short number) {
		ByteBuffer responseBodyBuffer = ByteBuffer.allocate(2);
		responseBodyBuffer.putShort(number);
		return responseBodyBuffer.array();
	}

	public static byte[] toByteArray(int number) {
		ByteBuffer responseBodyBuffer = ByteBuffer.allocate(4);
		responseBodyBuffer.putInt(number);
		return responseBodyBuffer.array();
	}

	public static short toShort(byte[] number) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(number);
		return byteBuffer.getShort();
	}

	public static int toInt(byte[] number) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(number);
		return byteBuffer.getInt();
	}

	public static byte[] concatByteArrays(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

}
