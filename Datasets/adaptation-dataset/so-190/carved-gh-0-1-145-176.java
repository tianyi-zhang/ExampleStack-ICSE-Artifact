public class foo{
	/**
	 * Strips unprintable characters and other special characters that might
	 * allow the user to manipulate the http header.
	 * 
	 * Code from:
	 * http://stackoverflow.com/questions/7161534/fastest-way-to-strip
	 * -all-non-printable-characters-from-a-java-string
	 * 
	 * @param s
	 * @return
	 * @throws Exception
	 */
	private String sanitizeFileName(String s) {
		final int length = s.length();
		if (oldChars.length < length) {
			oldChars = new char[length];
		}
		s.getChars(0, length, oldChars, 0);
		int newLen = 0;
		for (int j = 0; j < length; j++) {
			char ch = oldChars[j];
			if (ch >= ' ' && ch != ';') {
				oldChars[newLen] = ch;
				newLen++;
			}
		}
		if (newLen != length) {
			return new String(oldChars, 0, newLen);
		} else {
			return s;
		}
	}
}