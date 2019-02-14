public class foo{
    /**
     * get all the classes name in "classes.dex", "classes2.dex", ....
     *
     * @return all the classes name
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     */
    public static List<String> getAllClasses() throws PackageManager.NameNotFoundException, IOException {
        List<String> classNames = new ArrayList<>();
        for (String path : getSourcePaths()) {
            try {
                DexFile dexfile;
                if (path.endsWith(EXTRACTED_SUFFIX)) {
                    //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                    dexfile = DexFile.loadDex(path, path + ".tmp", 0);
                } else {
                    dexfile = new DexFile(path);
                }
                Enumeration<String> dexEntries = dexfile.entries();
                while (dexEntries.hasMoreElements()) {
                    classNames.add(dexEntries.nextElement());
                }
            } catch (IOException e) {
                throw new IOException("Error at loading dex file '" + path + "'");
            }
        }
        return classNames;
    }
}