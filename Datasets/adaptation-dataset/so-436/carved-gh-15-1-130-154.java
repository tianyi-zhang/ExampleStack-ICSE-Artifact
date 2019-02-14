public class foo{
	/**
	 * converts a hexadecimal string into an byte array
	 *
	 * @param hexString hexadecimal encoded string
	 * @return byte[]
	 */
	// http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java/140861#140861
	static byte[] hexStringToByteArray(String hexString) {
		String s = hexString;

		// handle uneven number of hex digits
		int len = s.length();
		if (len % 2 == 1) {
			s = "0" + s;
			len++;
		}

		// decode
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}