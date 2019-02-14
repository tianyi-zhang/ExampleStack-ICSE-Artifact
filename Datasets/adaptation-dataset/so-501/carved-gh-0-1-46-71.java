public class foo{
    /**
     * @param regex    The regular expression pattern to search on.
     * @param subject  The string to be replaced.
     * @param limit    The maximum number of replacements to make. A negative value
     *                 indicates replace all.
     * @param count    If this is not null, it will be set to the number of
     *                 replacements made.
     * @param callback Callback function
     */
    public static String replace(String regex, String subject, int limit,
            AtomicInteger count, Callback callback) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = Pattern.compile(regex).matcher(subject);
        int i;
        for (i = 0; (limit < 0 || i < limit) && matcher.find(); i++) {
            String replacement = callback.matchFound(matcher.toMatchResult());
            replacement = Matcher.quoteReplacement(replacement); //probably what you want...
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        if (count != null) {
            count.set(i);
        }
        return sb.toString();
    }
}