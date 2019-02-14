public class foo{
    /**
     * Returns a simple query map from a provided uri. The simple map enforces there is exactly one value for
     * every name (multiple values for the same name are regularly allowed in a set of parameters)
     * @param uri a uri, optionally with a query
     * @return a query map with a one to one mapping of names to values or empty {@link HashMap}
     * if no parameters are found on the uri
     * @see <a href="http://stackoverflow.com/a/13592567/1759443">StackOverflow</a>
     */
    public static Map<String, String> getSimpleQueryMap(String uri) {
        final Map<String, String> query_pairs = new LinkedHashMap<>();
        try {
            String query = uri.split("\\?")[1];
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                                URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        } catch (UnsupportedEncodingException e) {
            // Just print the trace, we don't want to crash the app. If you ever get an empty query params
            // map back, then we know there was a malformed URL returned from the api (or a failure) 1/27/16 [KV]
            e.printStackTrace();
        }

        return query_pairs;
    }
}