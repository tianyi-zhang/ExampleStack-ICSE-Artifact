public class foo{
	@Override
	public void putBitmap(String key, Bitmap data) {

		DiskLruCache.Editor editor = null;
		try {
			editor = mDiskCache.edit(key);
			if (editor == null) {
				return;
			}

			if (writeBitmapToFile(data, editor)) {
				mDiskCache.flush();
				editor.commit();
				if (LogA.isDebug()) {
					Log.d("cache_test_DISK_", "image put on disk cache " + key);
				}
			} else {
				editor.abort();
				if (LogA.isDebug()) {
					Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
				}
			}
		} catch (IOException e) {
			if (LogA.isDebug()) {
				Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + key);
			}
			try {
				if (editor != null) {
					editor.abort();
				}
			} catch (IOException ignored) {
			}
		}
	}
}