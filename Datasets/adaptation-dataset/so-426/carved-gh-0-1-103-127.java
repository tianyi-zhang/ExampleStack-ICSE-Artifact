public class foo{
	/**
	 * Code for getting IP address copied from:
	 * http://stackoverflow.com/questions/9573196
	 */
	@Thunk static String getWifiIpAddress() {
	  try {
	    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
	            .hasMoreElements();) {
	      NetworkInterface intf = en.nextElement();
	      if (intf.getName().contains("wlan")) {
	        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
	                .hasMoreElements();) {
	          InetAddress inetAddress = enumIpAddr.nextElement();
	          if (!inetAddress.isLoopbackAddress()
	                  && (inetAddress.getAddress().length == 4)) {
	            return inetAddress.getHostAddress();
	          }
	        }
	      }
	    }
	  } catch (Exception e) {
	    Debug.log(e);
	  }
	  return "0.0.0.0";
	}
}