public class foo{
    /**
     * Optimized method for converting a String into an Integer
     *
     * From http://stackoverflow.com/questions/1030479/most-efficient-way-of-converting-string-to-integer-in-java
     *
     * @param str the String holding an Integer value
     * @return the int value of str or Optional.empty() if not possible
     */
    public static Optional<Integer> intValueOfOptional(String str) {
        int idx = 0;
        int end;
        boolean sign = false;
        char ch;

        if ((str == null) || ((end = str.length()) == 0) || ((((ch = str.charAt(0)) < '0') || (ch > '9')) && (!(sign = ch == '-') || (++idx == end) || ((ch = str.charAt(idx)) < '0') || (ch > '9')))) {
            return Optional.empty();
        }

        int ival = 0;
        for (;; ival *= 10) {
            ival += '0' - ch;
            if (++idx == end) {
                return Optional.of(sign ? ival : -ival);
            }
            if (((ch = str.charAt(idx)) < '0') || (ch > '9')) {
                return Optional.empty();
            }
        }
    }
}