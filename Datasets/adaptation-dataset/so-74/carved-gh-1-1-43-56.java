public class foo{
    @Override
    public Map<String, List<String>> get(final URI uri,
            final Map<String, List<String>> requestHeaders) throws IOException {
        if (uri == null) {
            throw new IOException("uri is null.");
        }
        final String url = uri.toString();
        final Map<String, List<String>> res = new HashMap<>();
        final String cookie = webkitCookieManager.getCookie(url);
        if (cookie != null) {
            res.put("Cookie", Arrays.asList(cookie));
        }
        return res;
    }
}