public class foo{
	public void uncaughtException(Thread t, Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		String filename = new Date().getTime() + ".stacktrace";

		if (url != null) {
			sendToServer(stacktrace, filename);
		}
		if (localPath != null) {
			FileHelpers.writeToSDFile(filename, stacktrace);
		}
		if (null != t) {
			defaultUEH.uncaughtException(t, e);
		}
	}
}