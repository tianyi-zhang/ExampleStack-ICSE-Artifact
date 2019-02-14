public class foo{
    /*
     * borrowed the idea from:
     * https://stackoverflow.com/questions/3366281/tokenizing-a-string-but-ignoring-delimiters-within-quotes/3366603#3366603
     */
    public static String[] splitSpacesAndQuotes(String str, boolean skipOuterQuotes) {
        str += " "; // To detect last token when not quoted...
        ArrayList<String> strings = new ArrayList<String>();
        boolean inQuote = false;
        char quote = '\"';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\"' || c == '\'' || c == ' ' && !inQuote) {
                if (c == '\"' || c == '\'') {
                	if (!inQuote) {
                		quote = c;
                		inQuote=true;
                	} else {
                		if (quote == c) {
                			inQuote=false;                			
                		}
                	}
                    if (skipOuterQuotes || (inQuote && (quote != c))) {
                    	sb.append(c);
                    }
                }
                if (!inQuote && sb.length() > 0) {
               		strings.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            } else {
                sb.append(c);
            }
        }
        return strings.toArray(new String[strings.size()]);
    }
}