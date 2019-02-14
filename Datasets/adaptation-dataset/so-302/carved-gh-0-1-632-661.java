public class foo{
	// http://stackoverflow.com/questions/2368802/how-to-create-dynamic-template-string
	// Author: cletus http://stackoverflow.com/users/18393/cletus
	private static String replaceAll(String text, Map<String, String> params,
			char leading, char trailing) {
		String pattern = "";
		if (leading != 0) {
			pattern += leading;
		}
		pattern += "(\\w+)";
		if (trailing != 0) {
			pattern += trailing;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		boolean result = m.find();
		if (result) {
			StringBuffer sb = new StringBuffer();
			do {
				String replacement = params.get(m.group(1));
				if (replacement == null) {
					replacement = m.group();
				}
				m.appendReplacement(sb, replacement);
				result = m.find();
			} while (result);
			m.appendTail(sb);
			return sb.toString();
		}
		return text;
	}
}