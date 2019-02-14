public class foo {
public static Map <String, String> parseQueryString (final URL url)
        throws UnsupportedEncodingException
{
    final Map <String, String> qps = new TreeMap <String, String> ();
    final StringTokenizer pairs = new StringTokenizer (url.getQuery (), "&");
    while (pairs.hasMoreTokens ())
    {
        final String pair = pairs.nextToken ();
        final StringTokenizer parts = new StringTokenizer (pair, "=");
        final String name = URLDecoder.decode (parts.nextToken (), "ISO-8859-1");
        final String value = URLDecoder.decode (parts.nextToken (), "ISO-8859-1");
        qps.put (name, value);
    }
    return qps;
}
}