public class foo{
	public static String toHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEY_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEY_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
}