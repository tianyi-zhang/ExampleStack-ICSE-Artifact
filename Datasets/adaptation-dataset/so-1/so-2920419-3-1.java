public class foo {
    public synchronized String format(String s, int initialIndent)
    {
      int indent = initialIndent;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < s.length(); i++)
      {
        char currentChar = s.charAt(i);
        if (currentChar == '<')
        {
          char nextChar = s.charAt(i + 1);
          if (nextChar == '/')
            indent -= indentNumChars;
          if (!singleLine)   // Don't indent before closing element if we're creating opening and closing elements on a single line.
            sb.append(buildWhitespace(indent));
          if (nextChar != '?' && nextChar != '!' && nextChar != '/')
            indent += indentNumChars;
          singleLine = false;  // Reset flag.
        }
        sb.append(currentChar);
        if (currentChar == '>')
        {
          if (s.charAt(i - 1) == '/')
          {
            indent -= indentNumChars;
            sb.append("\n");
          }
          else
          {
            int nextStartElementPos = s.indexOf('<', i);
            if (nextStartElementPos > i + 1)
            {
              String textBetweenElements = s.substring(i + 1, nextStartElementPos);

              // If the space between elements is solely newlines, let them through to preserve additional newlines in source document.
              if (textBetweenElements.replaceAll("\n", "").length() == 0)
              {
                sb.append(textBetweenElements + "\n");
              }
              // Put tags and text on a single line if the text is short.
              else if (textBetweenElements.length() <= lineLength * 0.5)
              {
                sb.append(textBetweenElements);
                singleLine = true;
              }
              // For larger amounts of text, wrap lines to a maximum line length.
              else
              {
                sb.append("\n" + lineWrap(textBetweenElements, lineLength, indent, null) + "\n");
              }
              i = nextStartElementPos - 1;
            }
            else
            {
              sb.append("\n");
            }
          }
        }
      }
      return sb.toString();
    }
}