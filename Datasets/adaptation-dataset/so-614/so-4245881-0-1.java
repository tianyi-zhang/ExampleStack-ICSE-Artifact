public class foo {
private boolean isEqual(InputStream i1, InputStream i2) throws IOException {
    byte[] buf1 = new byte[64 *1024];
    byte[] buf2 = new byte[64 *1024];
    try {
        DataInputStream d2 = new DataInputStream(i2);
        int len;
        while ((len = i1.read(buf1)) > 0) {
            d2.readFully(buf2,0,len);
            for(int i=0;i<len;i++)
              if(buf1[i] != buf2[i]) return false;
        }
        return d2.read() < 0; // is the end of the second file also.
    } catch(EOFException ioe) {
        return false;
    } finally {
        i1.close();
        i2.close();
    }
}
}