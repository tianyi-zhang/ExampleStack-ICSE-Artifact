public class foo{
		@Override
		public void run() {
			mLocationManager.removeUpdates(locationListenerGps);
			mLocationManager.removeUpdates(locationListenerNetwork);

			Location net_loc  = null, gps_loc  = null;
			if (mGpsEnabled)
				gps_loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (mNetworkEnabled)
				net_loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

			//if there are both values use the latest one
			if (gps_loc != null && net_loc != null){
				if (gps_loc.getTime() > net_loc.getTime())
					mLocationResult.gotLocation(gps_loc);
				else
					mLocationResult.gotLocation(net_loc);
				return;
			}

			if (gps_loc != null) {
				mLocationResult.gotLocation(gps_loc);
				return;
			}

			if (net_loc != null) {
				mLocationResult.gotLocation(net_loc);
				return;
			}
			mLocationResult.gotLocation(null);
		}
}