public class foo{
		private static String hexify(byte bytes[]) {
			char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
					'9', 'a', 'b', 'c', 'd', 'e', 'f' };

			StringBuffer buf = new StringBuffer(bytes.length * 2);

			for (int i = 0; i < bytes.length; ++i) {
				buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
				buf.append(hexDigits[bytes[i] & 0x0f]);
			}

			return buf.toString();
		}
}