public class foo{
    protected String[] GetProtocolList() {
        String[] preferredProtocols = {"TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3"};
        String[] availableProtocols = null;

        SSLSocket socket = null;

        try {
            SSLSocketFactory factory = m_ctx.getSocketFactory();
            socket = (SSLSocket) factory.createSocket();

            availableProtocols = socket.getSupportedProtocols();
            Arrays.sort(availableProtocols);
        } catch (Exception e) {
            return new String[]{"TLSv1"};
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }

        List<String> aa = new ArrayList<String>();
        for (int i = 0; i < preferredProtocols.length; i++) {
            int idx = Arrays.binarySearch(availableProtocols, preferredProtocols[i]);
            if (idx >= 0)
                aa.add(preferredProtocols[i]);
        }

        return aa.toArray(new String[0]);
    }
}