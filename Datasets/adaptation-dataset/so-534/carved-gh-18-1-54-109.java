public class foo{
	/**
	 * Construct UnicodeReader, a Reader that skips
	 * the BOM in Unicode files (if present).
	 *
	 * @param in
	 *            Input stream.
	 * @param defaultEncoding
	 *            Default encoding to be used if BOM is not found, or <code>null</code> to use
	 *            system default encoding.
	 * @throws IOException
	 *             If an I/O error occurs.
	 */
	public UnicodeReader(InputStream in, Charset defaultEncoding) throws IOException {
		byte bom[] = new byte[BOM_SIZE];
		Charset encoding = null;
		int unread;
		PushbackInputStream pushbackStream = new PushbackInputStream(in, BOM_SIZE);
		int n = pushbackStream.read(bom, 0, bom.length);

		// Read ahead four bytes and check for BOM marks.
		if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			encoding = Charset.forName("UTF-8");
			unread = n - 3;
		} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			encoding = Charset.forName("UTF-16BE");
			unread = n - 2;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			encoding = Charset.forName("UTF-16LE");
			unread = n - 2;
		} else if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
				&& (bom[3] == (byte) 0xFF)) {
			encoding = Charset.forName("UTF-32BE");
			unread = n - 4;
		} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
				&& (bom[3] == (byte) 0x00)) {
			encoding = Charset.forName("UTF-32LE");
			unread = n - 4;
		} else {
			encoding = defaultEncoding;
			unread = n;
		}

		// Unread bytes if necessary and skip BOM marks.
		if (unread > 0) {
			pushbackStream.unread(bom, (n - unread), unread);
		} else if (unread < -1) {
			pushbackStream.unread(bom, 0, 0);
		}

		// Use given encoding.
		if (encoding == null) {
			reader = new InputStreamReader(pushbackStream, Charset.defaultCharset());
		} else {
			reader = new InputStreamReader(pushbackStream, encoding);
		}
	}
}