public class foo{
    /**
     * Extracts a {@link com.android.volley.Cache.Entry} from a {@link com.android.volley.NetworkResponse}.
     * Cache-control headers are ignored.
     * @param response The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     * @link http://stackoverflow.com/questions/16781244/android-volley-jsonobjectrequest-caching
     */
    public static Cache.Entry parseIgnoreCacheHeaders(NetworkResponse response,
                                                      long cacheRefreshTime,
                                                      long cacheExpiresTime) {
        long now = System.currentTimeMillis();

        Map<String, String> headers = response.headers;
        long serverDate = 0;
        String headerValue;

        headerValue = headers.get("Date");
        if (headerValue != null) {
            serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
        }

        final long softExpire = now + cacheRefreshTime;
        final long ttl = now + cacheExpiresTime;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = null; // Not worried about etag
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }
}