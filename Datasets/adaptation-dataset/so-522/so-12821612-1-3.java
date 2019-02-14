public class foo {
public SMTPTransport connectToSmtp(String host, int port, String userEmail,
        String oauthToken, boolean debug) throws Exception {

    Properties props = new Properties();
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.sasl.enable", "false");
    session = Session.getInstance(props);
    session.setDebug(debug);


    final URLName unusedUrlName = null;
    SMTPTransport transport = new SMTPTransport(session, unusedUrlName);
    // If the password is non-null, SMTP tries to do AUTH LOGIN.
    final String emptyPassword = null;
    transport.connect(host, port, userEmail, emptyPassword);

            byte[] response = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
            oauthToken).getBytes();
    response = BASE64EncoderStream.encode(response);

    transport.issueCommand("AUTH XOAUTH2 " + new String(response),
            235);

    return transport;
}
}