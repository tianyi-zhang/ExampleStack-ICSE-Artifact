public class foo{
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        DiskLruCache.Snapshot snapshot = null;
        try {

            snapshot = mDiskCache.get(CacheUtil.md5(url));
            if ( snapshot == null ) {
                return null;
            }
            final InputStream in = snapshot.getInputStream(0);
            if (in != null) {
                synchronized (sDecodeLock) {
                    try {
                        final BufferedInputStream buffIn =
                                new BufferedInputStream(in, IO_BUFFER_SIZE);
                        bitmap = BitmapFactory.decodeStream(buffIn);
                    } catch (OutOfMemoryError e) {
                        bitmap = null;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
        return bitmap;
    }
}