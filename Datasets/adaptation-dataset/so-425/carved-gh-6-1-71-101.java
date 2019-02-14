public class foo{
    @SuppressWarnings("unused")
    public void put(final String key, final Bitmap data) {
        if (diskCache == null) {
            return;
        }

        DiskLruCache.Editor editor = null;
        try {
            editor = diskCache.edit(key);
            if (editor == null)
                return;

            if (writeBitmapToFile(data, editor)) {
                editor.commit();
                if (BuildConfig.DEBUG && ImageManager.LOG_CACHE_OPERATIONS) {
                    Log.v(TAG, "image put on disk cache " + key);
                }
            } else {
                editor.abort();
                Log.e(TAG, "ERROR on: image put on disk cache " + key);
            }
        } catch (final IOException e) {
            Log.e(TAG, "ERROR on: image put on disk cache " + key, e);
            try {
                if (editor != null) {
                    editor.abort();
                }
            } catch (final IOException ignored) {} catch (final IllegalStateException ignored) {}
        }

    }
}