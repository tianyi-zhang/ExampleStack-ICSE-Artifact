public class foo{
	public boolean getLocation(final Context context, final LocationResult result) {
		//I use LocationResult callback class to pass location value from CurrentLocationHelper to user code.
		mLocationResult = result;
		if (mLocationManager ==  null)
			mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try {
			mGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (final Exception ex) {

		}

		try {
			mNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch(final Exception ex){

		}

		//don't start listeners if no provider is enabled
		if (!mGpsEnabled && !mNetworkEnabled)
			return false;

		if (mGpsEnabled)
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		if (mNetworkEnabled)
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		mTimer = new Timer();
		mTimer.schedule(new GetLastLocation(), 20000);
		return true;
	}
}