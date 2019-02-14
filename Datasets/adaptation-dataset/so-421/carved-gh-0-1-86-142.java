public class foo{
	    public void process(Document document) {
	        final Set<String> namespaces = new HashSet<String>();

	        Element element = document.getDocumentElement();
	        traverse(element, new ElementVisitor() {

	            public void visit(Element element) {
	                String namespace = element.getNamespaceURI();
	                if (namespace == null)
	                    namespace = "";
	                namespaces.add(namespace);
	                NamedNodeMap attributes = element.getAttributes();
	                for (int i = 0; i < attributes.getLength(); i++) {
	                    Node node = attributes.item(i);
	                    if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI()))
	                        continue;
	                    String prefix;
	                    if (XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI.equals(node.getNamespaceURI())) {
	                        if ("type".equals(node.getLocalName())) {
	                            String value = node.getNodeValue();
	                            if (value.contains(":"))
	                                prefix = value.substring(0, value.indexOf(":"));
	                            else
	                                prefix = null;
	                        } else {
	                            continue;
	                        }
	                    } else {
	                        prefix = node.getPrefix();
	                    }
	                    namespace = element.lookupNamespaceURI(prefix);
	                    if (namespace == null)
	                        namespace = "";
	                    namespaces.add(namespace);
	                }
	            }

	        });
	        traverse(element, new ElementVisitor() {

	            public void visit(Element element) {
	                Set<String> removeLocalNames = new HashSet<String>();
	                NamedNodeMap attributes = element.getAttributes();
	                for (int i = 0; i < attributes.getLength(); i++) {
	                    Node node = attributes.item(i);
	                    if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(node.getNamespaceURI()))
	                        continue;
	                    if (namespaces.contains(node.getNodeValue()))
	                        continue;
	                    removeLocalNames.add(node.getLocalName());
	                }
	                for (String localName : removeLocalNames)
	                    element.removeAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, localName);
	            }

	        });
	    }
}