public class foo{
	/**
	 * Adapted from http://stackoverflow.com/a/22980843/4248895.
	 */
	public static boolean isLocationEnabledForScanning_byOsServices(Context context)
	{
		if( Utils.isMarshmallow() )
		{
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
			{
				final int locationMode;

				try
				{
					locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
				}
				catch(Settings.SettingNotFoundException e)
				{
					return false;
				}

				return locationMode != Settings.Secure.LOCATION_MODE_OFF;
			}
			else
			{
				final String locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

				return false == TextUtils.isEmpty(locationProviders);
			}
		}
		else
		{
			return true;
		}
	}
}