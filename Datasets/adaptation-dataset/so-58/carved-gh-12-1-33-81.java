public class foo{
	/**
	 * Recursively add files to a JAR archive.
	 *
	 * @param directory The file/directory to add.
	 * @param file      File contained in the directory to add.
	 * @param remove    If true remove files we process.
	 * @throws IOException If something went wrong.
	 */
	public void add(String directory, String file, boolean remove) throws IOException {
		BufferedInputStream in = null;
		try {
			File source = new File(directory, file);
			if (source.isDirectory()) {
				String name = file.replace("\\", "/");
				if (!name.isEmpty()) {
					if (!name.endsWith("/"))
						name += "/";
					JarEntry entry = new JarEntry(name);
					entry.setTime(source.lastModified());
					target.putNextEntry(entry);
					target.closeEntry();
				}
				for (String nestedFile : source.list()) {
					add(directory, file + File.separator + nestedFile, remove);
				}
			} else {

				JarEntry entry = new JarEntry(file.replace("\\", "/"));
				entry.setTime(source.lastModified());
				target.putNextEntry(entry);
				in = new BufferedInputStream(new FileInputStream(source));

				byte[] buffer = new byte[1024];
				while (true) {
					int count = in.read(buffer);
					if (count == -1)
						break;
					target.write(buffer, 0, count);
				}
				target.closeEntry();
			}
			if (remove) {
				source.delete();
			}
		} finally {
			if (in != null)
				in.close();
		}
	}
}