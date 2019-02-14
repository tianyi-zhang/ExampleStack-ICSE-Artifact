public class foo {
public static String getManifestInfo() {
    Enumeration resEnum;
    try {
        resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
        while (resEnum.hasMoreElements()) {
            try {
                URL url = (URL)resEnum.nextElement();
                InputStream is = url.openStream();
                if (is != null) {
                    Manifest manifest = new Manifest(is);
                    Attributes mainAttribs = manifest.getMainAttributes();
                    String version = mainAttribs.getValue("Implementation-Version");
                    if(version != null) {
                        return version;
                    }
                }
            }
            catch (Exception e) {
                // Silently ignore wrong manifests on classpath?
            }
        }
    } catch (IOException e1) {
        // Silently ignore wrong manifests on classpath?
    }
    return null; 
}
}