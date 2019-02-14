public class foo{
	/**
	 * Thanks to http://stackoverflow.com/questions/3777055/reading-manifest-mf-file-from-jar-file-using-java
	 */
	private void logVersionInfo()
	{
		try
		{
			final Enumeration<URL> resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements())
			{
				try
				{
					final URL url = resEnum.nextElement();
					final InputStream is = url.openStream();
					if (is != null)
					{
						final Manifest manifest = new Manifest(is);
						final Attributes mainAttribs = manifest.getMainAttributes();
						final String version = mainAttribs.getValue("Implementation-Version");
						if (version != null)
						{
							logger.info("Resource " + url + " has version " + version);
						}
					}
				}
				catch (final Exception e)
				{
					// Silently ignore wrong manifests on classpath?
				}
			}
		}
		catch (final IOException e1)
		{
			// Silently ignore wrong manifests on classpath?
		}
	}
}