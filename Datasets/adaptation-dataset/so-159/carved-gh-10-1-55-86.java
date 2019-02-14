public class foo{
	/**
	 * Get IP address from first non-localhost interface
	 *
	 * @param useIPv4
	 *            true=return ipv4, false=return ipv6
	 * @return address or empty string
	 * @throws SocketException
	 */
	public static String getIPAddress(boolean useIPv4) throws SocketException {
		List<NetworkInterface> interfaces = Collections.list(NetworkInterface
			.getNetworkInterfaces());
		for (NetworkInterface intf : interfaces) {
			List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
			for (InetAddress addr : addrs) {
				if (!addr.isLoopbackAddress()) {
					String sAddr = addr.getHostAddress();
					boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
					if (useIPv4) {
						if (isIPv4) return sAddr;
					} else {
						if (!isIPv4) {
							// drop ip6 port suffix
							int delim = sAddr.indexOf('%');
							return delim < 0 ? sAddr : sAddr
								.substring(0, delim);
						}
					}
				}
			}
		}
		return "";
	}
}