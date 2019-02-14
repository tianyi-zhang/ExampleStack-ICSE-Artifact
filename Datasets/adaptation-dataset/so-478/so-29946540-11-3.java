public class foo {
    @Override
    public void setEnabledProtocols(String[] protocols) {
        if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {

            List<String> enabledProtocols = new ArrayList<String>(Arrays.asList(delegate.getEnabledProtocols()));
            if (enabledProtocols.size() > 1) {
                enabledProtocols.remove("SSLv3");
                System.out.println("Removed SSLv3 from enabled protocols");
            } else {
                System.out.println("SSL stuck with protocol available for " + String.valueOf(enabledProtocols));
            }
            protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
        }

        super.setEnabledProtocols(protocols);
    }
}