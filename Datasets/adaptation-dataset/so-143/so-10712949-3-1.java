public class foo {
@Override
protected void authenticate() throws IOException, XMPPException
{
    String authCode = password;
    String jidAndToken = "\0" + URLEncoder.encode( authenticationId, "utf-8" ) + "\0" + authCode;

    StringBuilder stanza = new StringBuilder();
    stanza.append( "<auth mechanism=\"" ).append( getName() );
    stanza.append( "\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" );
    stanza.append( new String(Base64.encode( jidAndToken.getBytes( "UTF-8" ), Base64.DEFAULT ) ) );

    stanza.append( "</auth>" );

    Log.v("BlueTalk", "Authentication text is "+stanza);
    // Send the authentication to the server
    getSASLAuthentication().send( new Auth2Mechanism(stanza.toString()) );
}
}