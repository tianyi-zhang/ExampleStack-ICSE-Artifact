public class foo{
    /**
     * get all the classes name in "classes.dex", "classes2.dex", ....
     *
     * @param context the application context
     * @return all the classes name
     * @throws PackageManager.NameNotFoundException
     * @throws IOException
     */
    static List<String> getAllClasses(Context context) throws PackageManager.NameNotFoundException, IOException {
        List<String> classNames = new ArrayList<>();
        for (String path : getSourcePaths(context)) {
            try {
                DexFile dexfile;
                String pathTmp = null;
                if (path.endsWith(EXTRACTED_SUFFIX)) {
                    //NOT use new DexFile(path), because it will throw "permission error in /data/dalvik-cache"
                    pathTmp = path + ".tmp";
                    dexfile = DexFile.loadDex(path, pathTmp, 0);
                } else {
                    dexfile = new DexFile(path);
                }
                Enumeration<String> dexEntries = dexfile.entries();
                while (dexEntries.hasMoreElements()) {
                    classNames.add(dexEntries.nextElement());
                }
                if (pathTmp != null)
                    Utility.deleteFile(new File(pathTmp));
            } catch (IOException e) {
                throw new IOException("Error at loading dex file '" +
                        path + "'");
            }
        }
        return classNames;
    }
}