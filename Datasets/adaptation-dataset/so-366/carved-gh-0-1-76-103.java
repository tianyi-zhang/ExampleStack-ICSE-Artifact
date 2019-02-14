public class foo{
     /**
      * Downloads a file from the Internet to the disk
      * @param filename
      * @param URL
      * @copyright Ben Noland
      * @retrieved 2015-08-29
      * @retriever Nuno Brito
      * @origin http://stackoverflow.com/questions/921262/how-to-download-and-save-a-file-from-internet-using-java
      */
    public static void downloadFile(final File filename, final String URL){
        try {
            BufferedInputStream in = null;
            FileOutputStream fout = null;

            in = new BufferedInputStream(new URL(URL).openStream());
            fout = new FileOutputStream(filename);

            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
                in.close();
                fout.close();
        }
        catch(Exception e){
        }
}
}