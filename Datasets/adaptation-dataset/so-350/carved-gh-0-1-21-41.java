public class foo{
	public static final String readRegistry(String location, String key) {
		try {
			Process process = Runtime.getRuntime().exec("reg query \"" + location + "\" /v " + key); //$NON-NLS-1$ //$NON-NLS-2$
			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();

			String output = reader.getResult();
			// Output has the following format:
			// \n<Version information>\n\n<key>\t<registry type>\t<value>
			if (!output.contains("\t")) { //$NON-NLS-1$
				return null;
			}
			// Parse out the value
			String[] parsed = output.split("\t"); //$NON-NLS-1$
			return parsed[parsed.length - 1];
		} catch (Exception e) {
			return null;
		}
	}
}