import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Stack;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * PositionalXMLReader
 * 
 * XML-Reader which can return XML Node at a given line/col location of a document
 * 
 * @link		http://stackoverflow.com/a/4918080/1128689
 * @date		2013-02-09
 */
public class PositionalXMLReader {
    final static String LINE_NUMBER_KEY_NAME = "lineNumber";
    final static String COLUMN_NUMBER_KEY_NAME = "columnNumber";
	protected static final String LINE_NUMBER_LAST_KEY_NAME = "lineNumberLast";
	protected static final String COLUMN_NUMBER_LAST_KEY_NAME = "columnNumberLast";
	private static final HashMap<String, Integer> ELEMENT_NAMES_COUNTER = new HashMap<String, Integer>();
	protected static final String ELEMENT_ID = "elementId";

    public static Document readXML(final InputStream is) throws IOException, SAXException {
        final Document doc;
        SAXParser parser;
        try {
            final SAXParserFactory factory = SAXParserFactory.newInstance();
            parser = factory.newSAXParser();
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
        } catch (final ParserConfigurationException e) {
            throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
        }

        final Stack<Element> elementStack = new Stack<Element>();
        final StringBuilder textBuffer = new StringBuilder();
        final DefaultHandler handler = new DefaultHandler() {
            private Locator locator;

            @Override
            public void setDocumentLocator(final Locator locator) {
                this.locator = locator; // Save the locator, so that it can be used later for line tracking when traversing nodes.
            }

            @Override
            public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
                    throws SAXException {
                addTextIfNeeded();
                final Element el = doc.createElement(qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    el.setAttribute(attributes.getQName(i), attributes.getValue(i));
                }
                el.setUserData(LINE_NUMBER_KEY_NAME, this.locator.getLineNumber(), null);
                el.setUserData(COLUMN_NUMBER_KEY_NAME, this.locator.getColumnNumber(), null);
                int counter = ELEMENT_NAMES_COUNTER.containsKey(qName) ? ELEMENT_NAMES_COUNTER.get(qName) : 1;
                el.setUserData(ELEMENT_ID, qName + "_" + counter++, null);
                ELEMENT_NAMES_COUNTER.put(qName, counter);
                elementStack.push(el);
            }

            @Override
            public void endElement(final String uri, final String localName, final String qName) {
                addTextIfNeeded();
                final Element closedEl = elementStack.pop();
                closedEl.setUserData(LINE_NUMBER_LAST_KEY_NAME, this.locator.getLineNumber(), null);
                closedEl.setUserData(COLUMN_NUMBER_LAST_KEY_NAME, this.locator.getColumnNumber(), null);
                if (elementStack.isEmpty()) { // Is this the root element?
                    doc.appendChild(closedEl);
                } else {
                    final Element parentEl = elementStack.peek();
                    parentEl.appendChild(closedEl);
                }
            }

            @Override
            public void characters(final char ch[], final int start, final int length) throws SAXException {
                textBuffer.append(ch, start, length);
            }

            // Outputs text accumulated under the current node
            private void addTextIfNeeded() {
                if (textBuffer.length() > 0) {
                    final Element el = elementStack.peek();
                    final Node textNode = doc.createTextNode(textBuffer.toString());
                    el.appendChild(textNode);
                    textBuffer.delete(0, textBuffer.length());
                }
            }
        };
        parser.parse(is, handler);

        return doc;
    }
}