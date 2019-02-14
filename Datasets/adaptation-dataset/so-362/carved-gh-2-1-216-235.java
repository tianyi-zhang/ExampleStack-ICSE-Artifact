public class foo{
    public List<String> getResourcesFromDirectory(File directory, Pattern pattern) {
        List<String> retval = new ArrayList<>();
        File[] fileList = directory.listFiles();
        for (File file : fileList) {
            if (file.isDirectory()) {
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else {
                try {
                    String fileName = file.getCanonicalPath();
                    boolean accept = pattern.matcher(fileName).matches();
                    if (accept) {
                        retval.add(fileName);
                    }
                } catch (IOException ioe) {
                    log.warn("IO Exception", ioe);
                }
            }
        }
        return retval;
    }
}