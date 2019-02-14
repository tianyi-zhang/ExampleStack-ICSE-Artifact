public class foo{
	public static Map<String, String> parseQueryString(String encodedParams)
			throws UnsupportedEncodingException {
		final Map<String, String> qps = new HashMap<String, String>();
		final StringTokenizer pairs = new StringTokenizer(encodedParams, "&");
		while (pairs.hasMoreTokens()) {
			final String pair = pairs.nextToken();
			final StringTokenizer parts = new StringTokenizer(pair, "=");
			final String key = URLDecoder.decode(parts.nextToken(), "UTF-8");
			final String value = parts.hasMoreTokens() ? URLDecoder.decode(parts.nextToken(), "UTF-8") : "";

			qps.put(key, value);
		}
		return qps;
	}
}