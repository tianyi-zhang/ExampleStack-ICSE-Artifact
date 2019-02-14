public class foo{
	public UnicodeInputStream(final InputStream inputStream, final boolean skipBom) throws NullPointerException, IOException {
		if (inputStream == null) {
			throw new NullPointerException("invalid input stream: null is not allowed");
		}

		in = new PushbackInputStream(inputStream, 4);

		final byte bytes[] = new byte[4];
		final int read = in.read(bytes);

		switch (read) {
		case 4:
			if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)
					&& (bytes[2] == (byte) 0x00) && (bytes[3] == (byte) 0x00)) {
				bom = BOM.UTF_32_LE;
				break;
			} else if ((bytes[0] == (byte) 0x00) && (bytes[1] == (byte) 0x00)
					&& (bytes[2] == (byte) 0xFE) && (bytes[3] == (byte) 0xFF)) {
				bom = BOM.UTF_32_BE;
				break;
			}

		case 3:
			if ((bytes[0] == (byte) 0xEF) && (bytes[1] == (byte) 0xBB)
					&& (bytes[2] == (byte) 0xBF)) {
				bom = BOM.UTF_8;
				break;
			}

		case 2:
			if ((bytes[0] == (byte) 0xFF) && (bytes[1] == (byte) 0xFE)) {
				bom = BOM.UTF_16_LE;
				break;
			} else if ((bytes[0] == (byte) 0xFE) && (bytes[1] == (byte) 0xFF)) {
				bom = BOM.UTF_16_BE;
				break;
			}

		default:
			bom = BOM.NONE;
			break;
		}

		if (read > 0) {
			in.unread(bytes, 0, read);
		}
		if (skipBom) {
			in.skip(bom.bytes.length);
		}
	}
}