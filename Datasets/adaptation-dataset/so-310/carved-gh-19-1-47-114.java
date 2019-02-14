public class foo{
    /**
     *
     * @param zipFile
     * @throws ZipException
     * @throws IOException
     */
    static public void unzip(String zipFile) throws ZipException, IOException {

        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = zipFile.substring(0, zipFile.length() - 4);
//simulates the unzip here feature
        newPath
                = newPath.substring(0,
                        newPath.lastIndexOf(File.separator));

        Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();
            BufferedOutputStream dest = null;
            BufferedInputStream is = null;
            try {
                if (!entry.isDirectory()) {
                    is = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;

                    // establish buffer for writing file
                    byte[] data = new byte[BUFFER];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    dest = new BufferedOutputStream(fos, BUFFER);

                    // read and write until last byte is encountered
                    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, currentByte);
                    }

                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage());
            } finally {
                if (dest != null) {
                    dest.flush();
                    dest.close();
                }
                if (is != null) {
                    is.close();
                }
            }

            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzip(destFile.getAbsolutePath());
            }
        }
    }
}