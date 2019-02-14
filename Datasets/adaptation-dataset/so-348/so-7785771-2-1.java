public class foo {
    public String extractText(Reader reader) {
        final ArrayList<String> list = new ArrayList<String>();

        HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
            public void handleText(final char[] data, final int pos) {
                list.add(new String(data));
            }

            public void handleStartTag(HTML.Tag tag, MutableAttributeSet attribute, int pos) {
            }

            public void handleEndTag(HTML.Tag t, final int pos) {
            }

            public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, final int pos) {
                if (t.equals(HTML.Tag.BR)) {
                    list.add("\n");
                }
            }

            public void handleComment(final char[] data, final int pos) {
            }

            public void handleError(final String errMsg, final int pos) {
            }
        };
        try {
            new ParserDelegator().parse(reader, parserCallback, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = "";
        for (String s : list) {
            result += s;
        }
        return result;
    }
}