public class foo{
	/**
	 * @return the Wifi IP address
	 * @see "http://stackoverflow.com/questions/16730711/get-my-wifi-ip-address-android"
	 */
	public String getWifiIpAddress() {
		final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

		// Convert little-endian to big-endianif needed
		if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			ipAddress = Integer.reverseBytes(ipAddress);
		}

		final byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

		String ipAddressString;
		try {
			ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
		} catch (UnknownHostException ex) {
			MyLog.warn("Unable to get host address.", ex);
			ipAddressString = null;
		}

		return ipAddressString;
	}
}