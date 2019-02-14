package br.com.app.template.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur on 05/08/15.
 *
 * A class with connection utils
 *
 */

@EBean
public class ConnectionUtils {
	
	private List<ConnectionType> androidConnetionsTypes;
	
	@SystemService
	ConnectivityManager cm;

    /**
     * Perform an connection vefication, to get the current type
     */
	public void performConnectionVerification() {
        androidConnetionsTypes = new ArrayList<>();

		try {
            NetworkInfo info = cm.getActiveNetworkInfo();
            if (info == null || !info.isConnected()){
                androidConnetionsTypes.add(ConnectionType.NO_CONNECTION);
                return;
            }

            if (info.getType()  ==  ConnectivityManager.TYPE_MOBILE) {
                androidConnetionsTypes.add(ConnectionType.MOBILE);
            }
            if(info.getType()  ==  ConnectivityManager.TYPE_WIFI){
                androidConnetionsTypes.add(ConnectionType.WIFI);
            }

            if ( info.getType() !=  ConnectivityManager.TYPE_MOBILE &&
                     info.getType()   != ConnectivityManager.TYPE_WIFI) {
                androidConnetionsTypes.add(ConnectionType.NO_CONNECTION);
            }

        } catch (Exception e) {
            androidConnetionsTypes.add(ConnectionType.NO_CONNECTION);
        }
	}

    /**
     * Get the best connection type from the Types get by <b>performConnectionVerification</b>
     * @see ConnectionUtils#performConnectionVerification
     * @return The best connection type
     */
    public ConnectionType bestConnection() {
        performConnectionVerification();
        if (androidConnetionsTypes.contains(ConnectionType.WIFI)) {
            return ConnectionType.WIFI;
        } else if (androidConnetionsTypes.contains(ConnectionType.MOBILE)) {
            return ConnectionType.MOBILE;
        } else {
            return ConnectionType.NO_CONNECTION;
        }
    }

    /**
     * Check if the connection is fast
     * @see \http://stackoverflow.com/questions/2802472/detect-network-connection-type-on-android
     * @return boolean
     */
    public boolean isConnectionFast(){

        int subType = 65000;
        ConnectionType connectionType = bestConnection();

        if (connectionType == ConnectionType.WIFI) {
            int type = ConnectivityManager.TYPE_WIFI;
            subType = cm.getNetworkInfo(type).getSubtype();
        } else if (connectionType == ConnectionType.MOBILE) {
            int type = ConnectivityManager.TYPE_MOBILE;
            subType = cm.getNetworkInfo(type).getSubtype();
        } else {
            return false;
        }

        if(connectionType == ConnectionType.WIFI){
            return true;
        }else if(connectionType == ConnectionType.MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }

    /**
     * @param host String with the host identification
     * @param port String with the port
     * @param timeout int with the try timeout in milliseconds
     * @return true if can open a socket, false if else
     */
    public static boolean hostReachableByTcp(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public List<ConnectionType> getAndroidConnetionsTypes() {
        return androidConnetionsTypes;
    }

    public NetworkInfo getNetworkInfo(){
        return cm.getActiveNetworkInfo();
    }
}
