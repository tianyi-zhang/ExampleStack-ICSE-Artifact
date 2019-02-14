public class foo{
	/**
	 * Reads a variable-length string of 1-byte characters.
	 */
	public String readString() throws IOException {
		// 00 = empty string
		// 0B <length> <char>* = normal string
		// <length> is encoded as an LEB, and is the byte length of the rest.
		// <char>* is encoded as UTF8, and is the string content.
		byte kind = this.reader.readByte();
		if (kind == 0)
			return "";
		if (kind != 0x0B)
			throw new IOException(String.format("String format error: Expected 0x0B or 0x00, found 0x%02X", kind & 0xFF));
		int length = readULEB128();
		if (length == 0)
			return "";
		byte[] utf8bytes = new byte[length];
		this.reader.readFully(utf8bytes);
		return new String(utf8bytes, "UTF-8");
	}
}