public class foo{
    // Adapted from http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
    @Nonnull
    public static String convertGlobToRegEx(@Nonnull String line) {
        line = line.trim();
        int strLen = line.length();
        StringBuilder sb = new StringBuilder(strLen);
        // Remove beginning and ending * globs because they're useless
        if (line.startsWith("*")) {
            line = line.substring(1);
            strLen--;
        }
        if (line.endsWith("*")) {
            line = line.substring(0, strLen - 1);
            strLen--;
        }
        boolean escaping = false;
        int inCurlies = 0;
        CHAR:
        for (char currentChar : line.toCharArray()) {
            switch (currentChar) {
                case '*':
                    if (escaping)
                        sb.append("\\*");
                    else
                        sb.append(".*");
                    break;
                case '?':
                    if (escaping)
                        sb.append("\\?");
                    else
                        sb.append('.');
                    break;
                case '.':
                case '(':
                case ')':
                case '+':
                case '|':
                case '^':
                case '$':
                case '@':
                case '%':
                    sb.append('\\');
                    sb.append(currentChar);
                    break;
                case '\\':
                    if (escaping)
                        sb.append("\\\\");
                    else {
                        escaping = true;
                        continue CHAR;
                    }
                    break;
                case '{':
                    if (escaping)
                        sb.append("\\{");
                    else {
                        sb.append('(');
                        inCurlies++;
                    }
                    break;
                case '}':
                    if (escaping)
                        sb.append("\\}");
                    else if (inCurlies > 0) {
                        sb.append(')');
                        inCurlies--;
                    } else
                        sb.append("}");
                    break;
                case ',':
                    if (escaping)
                        sb.append("\\,");
                    else if (inCurlies > 0)
                        sb.append('|');
                    else
                        sb.append(",");
                    break;
                default:
                    sb.append(currentChar);
                    break;
            }
            escaping = false;
        }
        return sb.toString();
    }
}