public class foo{
	// mostly based on stackoverflow answer from ajma :
	// http://stackoverflow.com/questions/9573196/how-to-get-the-ip-of-the-wifi-hotspot-in-android
    public String getWifiIp() {
    	Enumeration<NetworkInterface> en=null;
    	try {
    		 en = NetworkInterface.getNetworkInterfaces();
    	} catch (SocketException e) {
    		return null;
    	}
		while (en.hasMoreElements()) {
			NetworkInterface intf = en.nextElement();
			if (intf.getName().contains("wlan")) {
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)) {
						//Log.d("VotAR Main", inetAddress.getHostAddress());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		}
		return null;
    }
}