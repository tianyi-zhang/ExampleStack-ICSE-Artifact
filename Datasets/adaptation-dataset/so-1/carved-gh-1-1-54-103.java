public class foo{
	/**
	 * Formats the string (xml).
	 * @param s the xml string
	 * @return String
	 */
	public String format(String s) {
		int indent = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char currentChar = s.charAt(i);
			if (currentChar == '<') {
				char nextChar = s.charAt(i + 1);
				if (nextChar == '/')
					indent -= indentNumChars;
				if (!singleLine)
		            sb.append(this.createIndentation(indent));
				if (nextChar != '?' && nextChar != '!' && nextChar != '/')
					indent += indentNumChars;
				singleLine = false;
			}
			sb.append(currentChar);
			if (currentChar == '>') {
				if (s.charAt(i - 1) == '/') {
					indent -= indentNumChars;
					sb.append(NEW_LINE);
				} else {
					int nextStartElementPos = s.indexOf('<', i);
					if (nextStartElementPos > i + 1) {
						String textBetweenElements = s.substring(i + 1, nextStartElementPos);
						// If the space between elements is solely newlines,
						// let them through to preserve additional newlines
						// in source document.
						if (textBetweenElements.replaceAll("(\n|\r\n|\r)", "").length() == 0) {
							sb.append(textBetweenElements + NEW_LINE);
						}
						// Put tags and text on a single line if the text is
						// short.
						else {
							sb.append(textBetweenElements);
							singleLine = true;
						}
						i = nextStartElementPos - 1;
					} else {
						sb.append(NEW_LINE);
					}
				}
			}
		}
		return sb.toString();
	}
}