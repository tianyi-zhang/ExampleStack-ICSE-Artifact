public class foo{
@Override
protected void authenticate() throws IOException, XMPPException
{
   //Log.d(NAME, "authId=" + authenticationId + "; password=" + password);

    String jidAndToken = "\0" + URLEncoder.encode( authenticationId, "utf-8" ) + "\0" + password;

    StringBuilder stanza = new StringBuilder();
    stanza.append( "<auth mechanism=\"" ).append( getName() );
    stanza.append( "\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">" );

    stanza.append( new String(Base64.encodeBase64( jidAndToken.getBytes( "UTF-8" ) ) ));
    stanza.append( "</auth>" );

    getSASLAuthentication().send( new Auth2Mechanism(stanza.toString()) );

}
}