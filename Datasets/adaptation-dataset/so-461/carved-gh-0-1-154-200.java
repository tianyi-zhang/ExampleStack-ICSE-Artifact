public class foo{
    /**
     * @author McDowell - http://stackoverflow.com/questions/1399126/java-util-zip -recreating-directory-structure
     * 
     * @param directory
     *            The directory to zip the contents of. Content structure will be preserved.
     * @param zipfile
     *            The zip file to output to.
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public void zipFolderContents(File directory, File zipfile) throws IOException
    {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<File>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;
        try
        {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;
            while (!queue.isEmpty())
            {
                directory = queue.pop();
                for (File child : directory.listFiles())
                {
                    String name = base.relativize(child.toURI()).getPath();
                    if (child.isDirectory())
                    {
                        queue.push(child);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    }
                    else
                    {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(child, zout);
                        zout.closeEntry();
                    }
                }
            }
        }
        finally
        {
            res.close();
        }
    }
}