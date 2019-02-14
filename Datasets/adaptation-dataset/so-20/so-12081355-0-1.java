public class foo {
/**
 * Returns a set of the unique names of all query parameters. Iterating
 * over the set will return the names in order of their first occurrence.
 *
 * @throws UnsupportedOperationException if this isn't a hierarchical URI
 *
 * @return a set of decoded names
 */
private Set<String> getQueryParameterNames(Uri uri) {
    if (uri.isOpaque()) {
        throw new UnsupportedOperationException("This isn't a hierarchical URI.");
    }

    String query = uri.getEncodedQuery();
    if (query == null) {
        return Collections.emptySet();
    }

    Set<String> names = new LinkedHashSet<String>();
    int start = 0;
    do {
        int next = query.indexOf('&', start);
        int end = (next == -1) ? query.length() : next;

        int separator = query.indexOf('=', start);
        if (separator > end || separator == -1) {
            separator = end;
        }

        String name = query.substring(start, separator);
        names.add(Uri.decode(name));

        // Move start to end of name.
        start = end + 1;
    } while (start < query.length());

    return Collections.unmodifiableSet(names);
}
}