public class foo{
        /**
         * This works around a bug in Android < 19 where SSLv3 is forced
         */
        @Override
        public void setEnabledProtocols(String[] protocols) {
            if (protocols != null && protocols.length == 1 && "SSLv3".equals(protocols[0])) {
                List<String> systemProtocols;
                if (this.compatible) {
                    systemProtocols = Arrays.asList(delegate.getEnabledProtocols());
                } else {
                    systemProtocols = Arrays.asList(delegate.getSupportedProtocols());
                }
                List<String> enabledProtocols = new ArrayList<String>(systemProtocols);
                if (enabledProtocols.size() > 1) {
                    enabledProtocols.remove("SSLv2");
                    enabledProtocols.remove("SSLv3");
                } else {
                    Log.w(TAG, "SSL stuck with protocol available for "
                            + String.valueOf(enabledProtocols));
                }
                protocols = enabledProtocols.toArray(new String[enabledProtocols.size()]);
            }
            super.setEnabledProtocols(protocols);
        }
}