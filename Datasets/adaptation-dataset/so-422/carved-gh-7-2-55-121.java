public class foo{
	public static List<StorageInfo> getStorageList() {

		List<StorageInfo> list = new ArrayList<StorageInfo>();
		String path = Environment.getExternalStorageDirectory().getPath();
		boolean isPathRemovable = Environment.isExternalStorageRemovable();
		String pathState = Environment.getExternalStorageState();
		boolean ifPathAvailable = pathState.equals(Environment.MEDIA_MOUNTED)
				|| pathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
		boolean isPathReadonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);

		HashSet<String> paths = new HashSet<String>();
		int removableNumber = 1;

		if (ifPathAvailable) {
			paths.add(path);
			list.add(new StorageInfo(path, isPathReadonly, isPathRemovable, isPathRemovable ? removableNumber++ : -1));
		}

		BufferedReader bufReader = null;
		try {
			bufReader = new BufferedReader(new FileReader("/proc/mounts"));
			String line;
			while ((line = bufReader.readLine()) != null) {
				if (line.contains("vfat") || line.contains("/mnt")) {
					StringTokenizer tokens = new StringTokenizer(line, " ");

					tokens.nextToken(); // device

					String mountPoint = tokens.nextToken(); // mount point
					if (paths.contains(mountPoint)) {
						continue;
					}

					tokens.nextToken(); // file system

					List<String> flags = Arrays.asList(tokens.nextToken().split(",")); // flags

					boolean readonly = flags.contains("ro");

					if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure") && !line.contains("/mnt/asec") && !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper") && !line.contains("tmpfs")) {
							paths.add(mountPoint);
							list.add(new StorageInfo(mountPoint, readonly, true, removableNumber++));
						}
					}
				}
			}

		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} catch (IOException ex) {
			Log.e(TAG, "Error listing storages", ex);

		} finally {
			if (bufReader != null) {
				try {
					bufReader.close();
				} catch (IOException ex) {
					Log.e(TAG, "Error listing storages", ex);
				}
			}
		}

		return list;
	}
}