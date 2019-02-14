package gr.uoa.di.android.helpers.net;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Needed manifest permissions :
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 *
 * Test functions :
 * Addresses.getMACAddress("wlan0"); Addresses.getMACAddress("eth0");
 * Addresses.getIPAddress(true); // IPv4 Addresses.getIPAddress(false); // IPv6
 *
 * @see <a href= "http://stackoverflow.com/questions/6064510>How to get
 *      ip-address of the device</a >
 */
public final class Addresses {

	private Addresses() {}

	/**
	 * Returns the SSID of the network connected to or null if not connected to
	 * a wifi network. If the SSID is empty or consists of whitespace only also
	 * null is returned - possible ?
	 *
	 * @param ctx
	 *            a Context needed to access the System services
	 * @return the SSID of the network connected to or null
	 */
	public static String getCurrentSsid(Context ctx) {
		String ssid = null;
		if (Net.isWifiConnected(ctx)) {
			WifiManager wm = (WifiManager) ctx
				.getSystemService(Context.WIFI_SERVICE);
			final WifiInfo connectionInfo = wm.getConnectionInfo();
			if (connectionInfo != null) {
				ssid = connectionInfo.getSSID();
				if (ssid != null && "".equals(ssid.trim())) ssid = null;
			}
		}
		return ssid;
	}

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

	/**
	 * Returns MAC address of the given interface name. Use only in GINGERBREAD
	 * and above builds
	 *
	 * @param interfaceName
	 *            eth0, wlan0 or NULL=use first interface
	 * @return mac address or empty string
	 * @throws SocketException
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static String getMACAddress(String interfaceName)
			throws SocketException {
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
			throw new RuntimeException("You can use getMACAddress() only "
				+ "after API " + Build.VERSION_CODES.GINGERBREAD);
		}
		List<NetworkInterface> interfaces = Collections.list(NetworkInterface
			.getNetworkInterfaces());
		for (NetworkInterface intf : interfaces) {
			if (interfaceName != null) {
				if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
			}
			byte[] mac = intf.getHardwareAddress();
			if (mac == null) return "";
			StringBuilder buf = new StringBuilder();
			for (int idx = 0; idx < mac.length; idx++)
				buf.append(String.format("%02X:", mac[idx]));
			if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
			return buf.toString();
		}
		return "";
		/*
		 * try { // this is so Linux hack return
		 * loadFileAsString("/sys/class/net/" +interfaceName +
		 * "/address").toUpperCase().trim(); } catch (IOException ex) { return
		 * null; }
		 */
	}
}
