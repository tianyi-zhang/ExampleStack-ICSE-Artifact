public class foo{
    /**
     * Code from
     * http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java
     *
     * @param filename name of file
     * @return line count
     * @throws IOException
     */
    static int count(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists())
            return 0;
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
            }
            return count;
        } finally {
            try{is.close();} catch(Exception e){/* do nothing */}
        }
    }
}