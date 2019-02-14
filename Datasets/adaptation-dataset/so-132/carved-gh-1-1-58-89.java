public class foo{
	public static boolean copyJarResourcesRecursively(final File destDir, final JarURLConnection jarConnection, FilenameFilter filter)
			throws IOException {

		final JarFile jarFile = jarConnection.getJarFile();

		for (final Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
			final JarEntry entry = e.nextElement();
			String startName = jarConnection.getEntryName();
			if (startName == null) {
				startName = "";
			}
			if (entry.getName().startsWith(startName)) {
				final String filename = removeStart(entry.getName(), //
						startName);

				if (!entry.isDirectory()) {
					final File f = new File(destDir, filename);
					final InputStream entryInputStream = jarFile.getInputStream(entry);
					if (filter.accept(destDir, filename)) {
						if (!ensureDirectoryExists(f.getParentFile())) {
							throw new IOException("Could not create directory: " + f.getParentFile().getAbsolutePath());
						}
						if (!copyStream(entryInputStream, f)) {
							return false;
						}
					}
					entryInputStream.close();
				}
			}
		}
		return true;
	}
}