public class foo{
    @Override
    public void put(final URI uri, final Map<String, List<String>> responseHeaders)
            throws IOException {
        if (uri == null || responseHeaders == null) {
            return;
        }
        final String url = uri.toString();
        for (final String headerKey : responseHeaders.keySet()) {
            if ((headerKey == null) || !(headerKey.equalsIgnoreCase("Set-Cookie2")
                    || headerKey.equalsIgnoreCase("Set-Cookie"))) {
                continue;
            }
            for (final String headerValue : responseHeaders.get(headerKey)) {
                webkitCookieManager.setCookie(url, headerValue);
            }
        }
    }
}