public class foo{
    /**
     * Constructs a new <code>UnicodeBOMInputStream</code> that wraps the
     * specified <code>InputStream</code>.
     *
     * @param inputStream
     *            an <code>InputStream</code>.
     *
     * @throws NullPointerException
     *             when <code>inputStream</code> is <code>null</code>.
     * @throws IOException
     *             on reading from the specified <code>InputStream</code> when
     *             trying to detect the Unicode BOM.
     */
    public UnicodeBomInputStream(final InputStream inputStream) throws NullPointerException,
            IOException

    {
        if (inputStream == null) {
            throw new NullPointerException("invalid input stream: null is not allowed");
        }

        in = new PushbackInputStream(inputStream, 4);

        final byte bom[] = new byte[4];
        final int read = in.read(bom);

        switch (read) {
        case 4:
            if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE && bom[2] == (byte) 0x00
                    && bom[3] == (byte) 0x00) {
                this.bom = BOM.UTF_32_LE;
                break;
            } else if (bom[0] == (byte) 0x00 && bom[1] == (byte) 0x00 && bom[2] == (byte) 0xFE
                    && bom[3] == (byte) 0xFF) {
                this.bom = BOM.UTF_32_BE;
                break;
            }

        case 3:
            if (bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF) {
                this.bom = BOM.UTF_8;
                break;
            }

        case 2:
            if (bom[0] == (byte) 0xFF && bom[1] == (byte) 0xFE) {
                this.bom = BOM.UTF_16_LE;
                break;
            } else if (bom[0] == (byte) 0xFE && bom[1] == (byte) 0xFF) {
                this.bom = BOM.UTF_16_BE;
                break;
            }

        default:
            this.bom = BOM.NONE;
            break;
        }

        if (read > 0) {
            in.unread(bom, 0, read);
        }
    }
}