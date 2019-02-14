public class foo{
	public SAXHandler() throws SAXException {
        try {
            final Transformer t = tf.newTransformer();
            
            os = new ByteArrayOutputStream(); 
            
            log.info("Using transformer: " + t.getClass().getName());
            	// org.apache.xalan.transformer.TransformerIdentityImpl works

            t.transform(new SAXSource(                
                new XMLReader() {     
                    public ContentHandler getContentHandler() { return ch; }
                    public DTDHandler getDTDHandler() { return null; }      
                    public EntityResolver getEntityResolver() { return null; }
                    public ErrorHandler getErrorHandler() { return null; }    
                    public boolean getFeature(String name) { return false; }
                    public Object getProperty(String name) { return null; } 
                    public void parse(InputSource input) { }               
                    public void parse(String systemId) { }  
                    
                    public void setContentHandler(ContentHandler handler) { ch = handler; }  
                    
                    public void setDTDHandler(DTDHandler handler) { }
                    public void setEntityResolver(EntityResolver resolver) { }
                    public void setErrorHandler(ErrorHandler handler) { }
                    public void setFeature(String name, boolean value) { }
                    public void setProperty(String name, Object value) { }
                }, new InputSource()),                                    
                new StreamResult(os));
        }
        catch (TransformerException e) {
            throw new SAXException(e);  
        }

        if (ch == null)
            throw new SAXException("Transformer didn't set ContentHandler");
    }
}