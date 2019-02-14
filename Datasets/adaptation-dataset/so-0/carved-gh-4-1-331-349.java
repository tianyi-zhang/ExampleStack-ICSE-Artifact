public class foo{
public final static
String uniplus(String s) {
    if (s.length() == 0) {
        return "";
    }
    /* This is just the minimum; sb will grow as needed. */
    StringBuffer sb = new StringBuffer(2 + 3 * s.length());
    sb.append("U+");
    for (int i = 0; i < s.length(); i++) {
        sb.append(String.format("%X", s.codePointAt(i)));
        if (s.codePointAt(i) > Character.MAX_VALUE) {
            i++; /****WE HATES UTF-16! WE HATES IT FOREVERSES!!!****/
        }
        if (i+1 < s.length()) {
            sb.append(".");
        }
    }
    return sb.toString();
}
}