public class foo{
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)
			throws FileNotFoundException {

		ParcelFileDescriptor[] pipe = null;

		String url = uri.getPath();

		try {
			String decodedUrl = URLDecoder.decode(url.replaceFirst("/", ""),
					"UTF-8");
			pipe = ParcelFileDescriptor.createPipe();

			HttpURLConnection connection = httpClient.open(new URL(decodedUrl));

			new TransferThread(connection.getInputStream(),
					new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]))
					.start();
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Exception opening pipe", e);
			throw new FileNotFoundException("Could not open pipe for: "
					+ uri.toString());
		}

		return (pipe[0]);
	}
}