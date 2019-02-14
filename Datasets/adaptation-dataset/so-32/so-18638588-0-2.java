public class foo {
protected String wifiIpAddress(Context context) {
    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

    // Convert little-endian to big-endianif needed
    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
        ipAddress = Integer.reverseBytes(ipAddress);
    }

    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

    String ipAddressString;
    try {
        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
    } catch (UnknownHostException ex) {
        Log.e("WIFIIP", "Unable to get host address.");
        ipAddressString = null;
    }

    return ipAddressString;
}
}