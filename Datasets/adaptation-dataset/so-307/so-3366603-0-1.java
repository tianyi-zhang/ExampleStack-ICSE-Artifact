public class foo {
public static String[] split(String str) {
    str += " "; // To detect last token when not quoted...
    ArrayList<String> strings = new ArrayList<String>();
    boolean inQuote = false;
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < str.length(); i++) {
        char c = str.charAt(i);
        if (c == '\"' || c == ' ' && !inQuote) {
            if (c == '\"')
                inQuote = !inQuote;
            if (!inQuote && sb.length() > 0) {
                strings.add(sb.toString());
                sb.delete(0, sb.length());
            }
        } else
            sb.append(c);
    }
    return strings.toArray(new String[strings.size()]);
}
}