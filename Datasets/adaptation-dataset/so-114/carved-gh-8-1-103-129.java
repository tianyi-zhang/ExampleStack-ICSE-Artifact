public class foo{
    public Bitmap getBitmap(final String key) {
        if (diskCache == null) {
            return null;
        }

        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = diskCache.get(key);
            if (snapshot == null)
                return null;
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                final BufferedInputStream buffIn = new BufferedInputStream(in, Utils.IO_BUFFER_SIZE);
                bitmap = BitmapProcessor.decodeStream(buffIn);
            }
        } catch (final IOException e) {
            Log.e(TAG, "ERROR getBitmap", e);
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }

        return bitmap;
    }
}