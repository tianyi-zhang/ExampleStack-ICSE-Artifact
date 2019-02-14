public class foo{
    private boolean isCompletelyWritten(File file) {
        // this was taken from http://stackoverflow.com/a/11242648
        RandomAccessFile stream = null;
        try {
            stream = new RandomAccessFile(file, "rw");
            return true;
        } catch (Exception e) {
            Log.d("INFO", "Skipping file " + file.getName() + " for now due to it not being completely written.");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.d("INFO", "Exception during closing file " + file.getName());
                }
            }
        }
        return false;
    }
}