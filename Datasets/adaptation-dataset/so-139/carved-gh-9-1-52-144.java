public class foo{
	/**
	 * Get the relative path from one file to another, specifying the directory
	 * separator. If one of the provided resources does not exist, it is assumed
	 * to be a file unless it ends with '/' or '\'.
	 * 
	 * @param targetPath
	 *            targetPath is calculated to this file
	 * @param basePath
	 *            basePath is calculated from this file
	 * @param pathSeparator
	 *            directory separator. The platform default is not assumed so
	 *            that we can test Unix behavior when running on Windows (for
	 *            example)
	 * @return resulting relative path string
	 */
	public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {
		// Normalize the paths
		String normalizedTargetPath = normalizeNoEndSeparator(targetPath);
		String normalizedBasePath = normalizeNoEndSeparator(basePath);
		char pathSeparatorCh;

		// Undo the changes to the separators made by normalization
		if (pathSeparator.equals("/")) { //$NON-NLS-1$
			pathSeparatorCh = '/';
			normalizedTargetPath = separatorsToUnix(normalizedTargetPath);
			normalizedBasePath = separatorsToUnix(normalizedBasePath);
		} else if (pathSeparator.equals("\\")) { //$NON-NLS-1$
			pathSeparatorCh = '\\';
			normalizedTargetPath = separatorsToWindows(normalizedTargetPath);
			normalizedBasePath = separatorsToWindows(normalizedBasePath);
		} else {
			throw new IllegalArgumentException(CopiedFromOtherJars.getText("msg.error.unrecognizedDirSeparator", pathSeparator)); //$NON-NLS-1$ 
		}
		final String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
		final String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		final StringBuilder common = new StringBuilder();

		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].equals(base[commonIndex])) {
			common.append(target[commonIndex] + pathSeparator);
			commonIndex++;
		}
		if (commonIndex == 0) {
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized.
			final String msg = CopiedFromOtherJars.getText("msg.error.noCommonPath", normalizedTargetPath, normalizedBasePath); //$NON-NLS-1$
			throw new PathResolutionException(msg);
		}

		// The number of directories we have to backtrack depends on whether the base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		// 
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers to a file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist, but it's the best I can do
		boolean baseIsFile = true;

		final File baseResource = new File(normalizedBasePath);

		if (basePath.endsWith(pathSeparator)) {
			baseIsFile = false;
		} else if (baseResource.exists()) {
			baseIsFile = baseResource.isFile();
		}
		final StringBuilder relative = new StringBuilder();

		if (base.length != commonIndex) {
			final int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;
			for (int i = 0; i < numDirsUp; i++) {
				relative.append(".." + pathSeparator); //$NON-NLS-1$
			}
		}
		// 'common' has the slash on the end, 'normalizedTargetPath' does not.
		int len = common.length();
		if (len <= normalizedTargetPath.length()) {
			if (normalizedTargetPath.charAt(len) == pathSeparatorCh)
				len++;
			relative.append(normalizedTargetPath.substring(len));
		} else if (relative.length() < 1) {
			relative.append(".");
		} else {
			relative.deleteCharAt(relative.length() - 1);
		}
		return relative.toString();
	}
}