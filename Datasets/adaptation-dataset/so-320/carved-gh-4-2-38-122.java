public class foo{
    public static Document readXML(final InputStream is) throws IOException,
            SAXException
    {
        final Document doc;
        SAXParser parser;
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory
                    .newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException(
                    "Can't create SAX parser / DOM builder.", e);
        }

        final Stack<Element> elementStack = new Stack<Element>();
        final StringBuilder textBuffer = new StringBuilder();
        final DefaultHandler handler = new DefaultHandler()
        {
            private Locator locator;

            @Override
            public void setDocumentLocator(final Locator locator)
            {
                this.locator = locator; // Save the locator, so that it can be
                                        // used later for line tracking when
                                        // traversing nodes.
            }

            @Override
            public void startElement(final String uri, final String localName,
                    final String qName, final Attributes attributes)
                    throws SAXException
            {
                addTextIfNeeded();
                final Element el = doc.createElement(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    el.setAttribute(attributes.getQName(i),
                            attributes.getValue(i));
                }
                el.setUserData(LINE_NUMBER_KEY_NAME,
                        String.valueOf(this.locator.getLineNumber()), null);
                elementStack.push(el);
            }

            @Override
            public void endElement(final String uri, final String localName,
                    final String qName)
            {
                addTextIfNeeded();
                final Element closedEl = elementStack.pop();
                if (elementStack.isEmpty()) { // Is this the root element?
                    doc.appendChild(closedEl);
                } else {
                    final Element parentEl = elementStack.peek();
                    parentEl.appendChild(closedEl);
                }
            }

            @Override
            public void characters(final char ch[], final int start,
                    final int length) throws SAXException
            {
                textBuffer.append(ch, start, length);
            }

            // Outputs text accumulated under the current node
            private void addTextIfNeeded()
            {
                if (textBuffer.length() > 0) {
                    final Element el = elementStack.peek();
                    final Node textNode = doc.createTextNode(textBuffer
                            .toString());
                    el.appendChild(textNode);
                    textBuffer.delete(0, textBuffer.length());
                }
            }
        };
        parser.parse(is, handler);

        return doc;
    }
}