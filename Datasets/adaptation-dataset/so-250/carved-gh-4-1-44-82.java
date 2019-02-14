public class foo{
    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static String getMACAddress(String interfaceName) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            throw new IllegalStateException(
                    "You use getMACAddress() only after API "
                            + Build.VERSION_CODES.GINGERBREAD);
        }
        try {
            List<NetworkInterface> interfaces = Collections
                    .list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (SocketException e) {
        } // for now eat exceptions
        return "";
        /*
         * try { // this is so Linux hack return
		 * loadFileAsString("/sys/class/net/" +interfaceName +
		 * "/address").toUpperCase().trim(); } catch (IOException ex) { return
		 * null; }
		 */
    }
}