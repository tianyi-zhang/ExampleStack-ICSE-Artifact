public class foo{
	/**
	 * Converts a standard POSIX Shell globbing pattern into a regular expression
	 * pattern. The result can be used with the standard {@link java.util.regex} API to
	 * recognize strings which match the glob pattern.
	 * <p/>
	 * See also, the POSIX Shell language:
	 * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
	 * 
	 * @author Neil Traft (http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns)
	 * @author Piero Dalle Pezze (small edits at the end).
	 * 
	 * @param pattern A glob pattern.
	 * @param matchWholeString true if the whole string is matched, false if only a substring is matched.
	 * @return A regex pattern to recognize the given glob pattern.
	 */
	public static final String convertGlobToRegex(String pattern, boolean matchWholeString) {
	    StringBuilder sb = new StringBuilder(pattern.length());
	    int inGroup = 0;
	    int inClass = 0;
	    int firstIndexInClass = -1;
	    char[] arr = pattern.toCharArray();
	    for (int i = 0; i < arr.length; i++) {
	        char ch = arr[i];
	        switch (ch) {
	            case '\\':
	                if (++i >= arr.length) {
	                    sb.append('\\');
	                } else {
	                    char next = arr[i];
	                    switch (next) {
	                        case ',':
	                            // escape not needed
	                            break;
	                        case 'Q':
	                        case 'E':
	                            // extra escape needed
	                            sb.append('\\');
	                        default:
	                            sb.append('\\');
	                    }
	                    sb.append(next);
	                }
	                break;
	            case '*':
	                if (inClass == 0)
	                    sb.append(".*");
	                else
	                    sb.append('*');
	                break;
	            case '?':
	                if (inClass == 0)
	                    sb.append('.');
	                else
	                    sb.append('?');
	                break;
	            case '[':
	                inClass++;
	                firstIndexInClass = i+1;
	                sb.append('[');
	                break;
	            case ']':
	                inClass--;
	                sb.append(']');
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
	                if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
	                    sb.append('\\');
	                sb.append(ch);
	                break;
	            case '!':
	                if (firstIndexInClass == i)
	                    sb.append('^');
	                else
	                    sb.append('!');
	                break;
	            case '{':
	                inGroup++;
	                sb.append('(');
	                break;
	            case '}':
	                inGroup--;
	                sb.append(')');
	                break;
	            case ',':
	                if (inGroup > 0)
	                    sb.append('|');
	                else
	                    sb.append(',');
	                break;
	            default:
	                sb.append(ch);
	        }
	    }
	    
	    if(matchWholeString) {
	    	return "^" + sb.toString() + "$";
	    }
	    return sb.toString();
	}
}