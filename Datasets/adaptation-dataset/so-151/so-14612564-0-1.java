public class foo {
public void foo(){
File file = null;
    String resource = "/com/myorg/foo.xml";
    URL res = getClass().getResource(resource);
    if (res.toString().startsWith("jar:")) {
        try {
            InputStream input = getClass().getResourceAsStream(resource);
            file = File.createTempFile("tempfile", ".tmp");
            OutputStream out = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            file.deleteOnExit();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    } else {
        //this will probably work in your IDE, but not from a JAR
        file = new File(res.getFile());
    }

    if (file != null && !file.exists()) {
        throw new RuntimeException("Error: File " + file + " not found!");
    }
}
}