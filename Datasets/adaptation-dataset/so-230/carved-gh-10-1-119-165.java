public class foo{
  /**
   * For the given {@code url}, extract a mapping of query string parameter names to values.
   * <p>
   * Adapted from an implementation by BalusC and dfrankow, available at
   * http://stackoverflow.com/questions/1667278/parsing-query-strings-in-java.
   * 
   * @param url
   *          The URL from which parameters are extracted.
   * @return A mapping of query string parameter names to values. If {@code url} is {@code null}, an empty {@code Map}
   *         is returned.
   * @throws IllegalStateException
   *           If unable to URL-decode because the JVM doesn't support {@value #ENCODING_CHARSET}.
   */
  public static Map<String, List<String>> extractParametersFromUrl(String url) {
    if (url == null) {
      return emptyMap();
    }

    Map<String, List<String>> parameters = new HashMap<String, List<String>>();

    String[] urlParts = url.split("\\?");

    if (urlParts.length > 1) {
      String query = urlParts[1];

      for (String param : query.split("&")) {
        String[] pair = param.split("=");
        String key = urlDecode(pair[0]);
        String value = "";

        if (pair.length > 1) {
          value = urlDecode(pair[1]);
        }

        List<String> values = parameters.get(key);

        if (values == null) {
          values = new ArrayList<String>();
          parameters.put(key, values);
        }

        values.add(value);
      }
    }

    return parameters;
  }
}