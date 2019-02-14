public class foo {
public String getWifiApIpAddress() {
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
                        Log.d(TAG, inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        }
    } catch (SocketException ex) {
        Log.e(TAG, ex.toString());
    }
    return null;
}
}