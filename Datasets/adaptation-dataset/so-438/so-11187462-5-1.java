public class foo {
    /**
     * Writes the specified byte to this output stream. The general contract
     * for <code>write</code> is that one byte is written to the output
     * stream. The byte to be written is the eight low-order bits of the
     * argument <code>b</code>. The 24 high-order bits of <code>b</code> are
     * ignored.
     * 
     * @param b
     *            the <code>byte</code> to write
     */
    @Override
    public void write(final int b) throws IOException {
        if (hasBeenClosed) {
            throw new IOException("The stream has been closed.");
        }

        // don't log nulls
        if (b == 0) {
            return;
        }

        // would this be writing past the buffer?
        if (count == bufLength) {
            // grow the buffer
            final int newBufLength = bufLength + DEFAULT_BUFFER_LENGTH;
            final byte[] newBuf = new byte[newBufLength];

            System.arraycopy(buf, 0, newBuf, 0, bufLength);

            buf = newBuf;
            bufLength = newBufLength;
        }

        buf[count] = (byte) b;
        count++;
    }
}