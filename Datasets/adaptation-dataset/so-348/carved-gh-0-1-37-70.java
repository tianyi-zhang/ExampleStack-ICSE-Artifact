public class foo{
  private String extractText(Reader reader) {

    StringBuilder result = new StringBuilder();
    HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
      @Override
      public void handleText(char[] data, int pos) {
        result.append(data);
      }
      @Override
      public void handleStartTag(HTML.Tag tag, MutableAttributeSet attribute, int pos) {
      }
      @Override
      public void handleEndTag(HTML.Tag tag, int pos) {
      }
      @Override
      public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet a, int pos) {
        if (tag.equals(HTML.Tag.BR)) {
          result.append('\n');
        }
      }
      @Override
      public void handleComment(char[] data, int pos) {
      }
      @Override
      public void handleError(String errMsg, int pos) {
      }
    };
    try {
      new ParserDelegator().parse(reader, parserCallback, true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return result.toString();
  }
}