public class foo{
	/**
	 * From <a
	 * href="http://stackoverflow.com/a/1269907">http://stackoverflow.com
	 * /a/1269907</a>.
	 * 
	 * @param target
	 *            File to relativize
	 * @param baseDirectory
	 *            Base directory to relativize to (treated as a directory:
	 *            /dir/file.txt treated as /dir/file.txt/)
	 * @return Relativized file
	 * @throws IOException
	 *             If an IOException occurs when calling
	 *             {@link File#getCanonicalPath()}
	 */
	public static File getRelativeFile(File target, File baseDirectory) throws IOException
	{
		String[] baseComponents = baseDirectory.getCanonicalPath().split(Pattern.quote(File.separator));
		String[] targetComponents = target.getCanonicalPath().split(Pattern.quote(File.separator));

		// skip common components
		int index = 0;
		for (; index < targetComponents.length && index < baseComponents.length; ++index)
		{
			if (!targetComponents[index].equals(baseComponents[index]))
				break;
		}

		StringBuilder result = new StringBuilder();
		if (index != baseComponents.length)
		{
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i)
				result.append(".." + File.separator);
		}
		for (; index < targetComponents.length; ++index)
			result.append(targetComponents[index] + File.separator);
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\"))
		{
			// remove final path separator
			result.delete(result.length() - File.separator.length(), result.length());
		}
		return new File(result.toString());
	}
}