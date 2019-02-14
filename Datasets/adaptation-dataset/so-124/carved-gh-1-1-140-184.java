public class foo{
	// source: http://stackoverflow.com/questions/11281010/how-can-i-get-external-sd-card-path-for-android-4-0
	public static Set<String> getExternalMounts()
	{
		final Set<String> externalMounts = new HashSet<>();
		String regex = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
		String mountOutput = "";

		// run mount process
		try
		{
			final Process process = new ProcessBuilder().command("mount").redirectErrorStream(true).start();
			process.waitFor();
			final InputStream is = process.getInputStream();
			final byte[] buffer = new byte[1024];
			while(is.read(buffer) != -1)
			{
				mountOutput = mountOutput + new String(buffer);
			}
			is.close();
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}

		// parse mount output
		final String[] lines = mountOutput.split("\n");
		for(String line : lines)
		{
			if(!line.toLowerCase(Locale.US).contains("asec")) // skip lines with "asec"
			{
				if(line.matches(regex))
				{
					String[] parts = line.split(" ");
					for(String part : parts)
					{
						if(part.startsWith("/")) // starts with slash
							if(!part.toLowerCase(Locale.US).contains("vold")) // not contains "vold"
								externalMounts.add(part);
					}
				}
			}
		}
		return externalMounts;
	}
}