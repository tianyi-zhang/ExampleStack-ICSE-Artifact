public class foo {
private static boolean isEqual(InputStream i1, InputStream i2)
        throws IOException {

    ReadableByteChannel ch1 = Channels.newChannel(i1);
    ReadableByteChannel ch2 = Channels.newChannel(i2);

    ByteBuffer buf1 = ByteBuffer.allocateDirect(1024);
    ByteBuffer buf2 = ByteBuffer.allocateDirect(1024);

    try {
        while (true) {

            int n1 = ch1.read(buf1);
            int n2 = ch2.read(buf2);

            if (n1 == -1 || n2 == -1) return n1 == n2;

            buf1.flip();
            buf2.flip();

            for (int i = 0; i < Math.min(n1, n2); i++)
                if (buf1.get() != buf2.get())
                    return false;

            buf1.compact();
            buf2.compact();
        }

    } finally {
        if (i1 != null) i1.close();
        if (i2 != null) i2.close();
    }
}
}