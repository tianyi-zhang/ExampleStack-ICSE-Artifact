public class foo {
    @Override
    protected void authenticate() throws IOException, XMPPException
    {
        String authCode = getAuthCode( authenticationId, password );
        String jidAndToken = "\0" + URLEncoder.encode( authenticationId, "utf-8" ) + "\0" + authCode;

        StringBuilder stanza = new StringBuilder();
        stanza.append( "<auth mechanism=\"" ).append( getName() );
        stanza.append( "\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" );
        stanza.append( Base64.encode( jidAndToken.getBytes( "UTF-8" ) ) );

        stanza.append( "</auth>" );

        // Send the authentication to the server
        getSASLAuthentication().send( stanza.toString() );
    }
}