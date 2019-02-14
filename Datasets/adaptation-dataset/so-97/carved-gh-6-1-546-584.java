public class foo{
	/**
	 * Takes a query string, separates the constituent name-value pairs, and stores them in a SortedMap ordered by lexicographical order.
	 * 
	 * @return Null if there is no query string.
	 */
	private static SortedMap<String, String> CreateParameterMap(final String queryString) {
		if (queryString == null || queryString.isEmpty())
			return null;
		final String[] pairs = queryString.split("&");
		final Map<String, String> params = new HashMap<String, String>(pairs.length);
		for (final String pair : pairs) {
			if (pair.length() < 1)
				continue;
			String[] tokens = pair.split("=", 2);
			for (int j = 0; j < tokens.length; j++) {
				try {
					tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
				}
				catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
			}
			switch (tokens.length) {
				case 0:
					break;
				case 1:
					if (pair.charAt(0) == '=')
						params.put("", tokens[0]);
					else
						params.put(tokens[0], "");
					break;
				case 2:
				default:
					params.put(tokens[0], tokens[1]);
					break;
			}
		}
		return new TreeMap<String, String>(params);
	}
}