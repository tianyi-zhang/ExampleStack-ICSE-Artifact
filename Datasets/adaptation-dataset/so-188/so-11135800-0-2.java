public class foo {
public void copyDirectory(File sourceLocation , File targetLocation)
throws IOException {

    if (sourceLocation.isDirectory()) {
        if (!targetLocation.exists() && !targetLocation.mkdirs()) {
            throw new IOException("Cannot create dir " + targetLocation.getAbsolutePath());
        }

        String[] children = sourceLocation.list();
        for (int i=0; i<children.length; i++) {
            copyDirectory(new File(sourceLocation, children[i]),
                    new File(targetLocation, children[i]));
        }
    } else {

        // make sure the directory we plan to store the recording in exists
        File directory = targetLocation.getParentFile();
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            throw new IOException("Cannot create dir " + directory.getAbsolutePath());
        }

        InputStream in = new FileInputStream(sourceLocation);
        OutputStream out = new FileOutputStream(targetLocation);

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
}