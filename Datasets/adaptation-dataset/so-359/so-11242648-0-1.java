public class foo {
private boolean isCompletelyWritten(File file) {
    RandomAccessFile stream = null;
    try {
        stream = new RandomAccessFile(file, "rw");
        return true;
    } catch (Exception e) {
        log.info("Skipping file " + file.getName() + " for this iteration due it's not completely written");
    } finally {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                log.error("Exception during closing file " + file.getName());
            }
        }
    }
    return false;
}
}