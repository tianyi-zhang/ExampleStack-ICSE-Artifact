public class foo{
	public DeviceUUIDFactory(Context context)
	{
		if(sUuid == null)
		{
			synchronized(DeviceUUIDFactory.class)
			{
				if(sUuid == null)
				{
					final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
					final String id = prefs.getString(PREFS_DEVICE_ID, null);

					if(id != null)
					{
						// use the ids previously computed and stored in the prefs file
						sUuid = UUID.fromString(id);
					}
					else
					{
						final String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);

						// Use the Android ID unless it's broken, in which case fallback on deviceId,
						// unless it's not available, then fallback on a random number which we store to a prefs file
						try
						{
							if(!"9774d56d682e549c".equals(androidId))
							{
								sUuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
							}
							else
							{
								// requires android.permission.READ_PHONE_STATE 
								final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
								sUuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
							}
						}
						catch(UnsupportedEncodingException e)
						{
							throw new RuntimeException(e);
						}

						// write the value out to the prefs file
						prefs.edit().putString(PREFS_DEVICE_ID, sUuid.toString()).commit();
					}
				}
			}
		}
	}
}