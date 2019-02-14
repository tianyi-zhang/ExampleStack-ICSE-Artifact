public class foo {
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (getRequestorType() == RequestorType.PROXY) {
            String prot = getRequestingProtocol().toLowerCase();
            String host = System.getProperty(prot + ".proxyHost", "");
            String port = System.getProperty(prot + ".proxyPort", "80");
            String user = System.getProperty(prot + ".proxyUser", "");
            String password = System.getProperty(prot + ".proxyPassword", "");

            if (getRequestingHost().equalsIgnoreCase(host)) {
                if (Integer.parseInt(port) == getRequestingPort()) {
                    // Seems to be OK.
                    return new PasswordAuthentication(user, password.toCharArray());  
                }
            }
        }
        return null;
    }
}