public class foo{
	private static boolean copyFilesRecusively(final File toCopy, final File destDir, FilenameFilter filter) {
		assert destDir.isDirectory();

		if (!toCopy.isDirectory()) {
			if (filter.accept(toCopy.getParentFile(), toCopy.getName())) {
				return copyFile(toCopy, new File(destDir, toCopy.getName()));
			}
			return true;
		} else {
			final File newDestDir = new File(destDir, toCopy.getName());
			if (!newDestDir.exists() && !newDestDir.mkdir()) {
				return false;
			}
			for (final File child : toCopy.listFiles()) {
				if (!copyFilesRecusively(child, newDestDir, filter)) {
					return false;
				}
			}
		}
		return true;
	}
}