public class foo{
	/**
	 * Normalization code courtesy of 'Mike Houston' http://stackoverflow.com/questions/2993649/how-to-normalize-a-url-in-java
	 */
	public static String NormalizeURL(final String taintedURL) throws MalformedURLException {
		final URL url;
		try {
			url = new URI(taintedURL).normalize().toURL();
		}
		catch (URISyntaxException e) {
			throw new MalformedURLException(e.getMessage());
		}

		final String path = url.getPath().replace("/$", "");
		final SortedMap<String, String> params = CreateParameterMap(url.getQuery());
		final int port = url.getPort();
		final String queryString;

		if (params != null) {
			// Some params are only relevant for user tracking, so remove the most commons ones.
			for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
				final String key = i.next();
				if (key.startsWith("utm_") || key.contains("session"))
					i.remove();
			}
			queryString = "?" + Canonicalize(params);
		}
		else
			queryString = "";

		return url.getProtocol() + "://" + url.getHost() + (port != -1 && port != 80 ? ":" + port : "") + path + queryString;
	}
}