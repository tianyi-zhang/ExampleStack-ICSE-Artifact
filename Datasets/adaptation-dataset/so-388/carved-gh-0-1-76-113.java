public class foo{
	/*
	 * Code from http://stackoverflow.com/questions/3590955/intent-to-launch-the-clock-application-on-android
	 * by frusso
	 */
	static private PendingIntent createPendingIntent(final Context context) {
		PackageManager packageManager = context.getPackageManager();
	    Intent alarmClockIntent = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER);
	    
	    String clockImpls[][] = {
	            {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
	            {"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
	            {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
	            {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
	            {"Samsung Galaxy Alarm Clock", "com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage"}
	    };

	    boolean foundClockImpl = false;

	    for(int i=0; i<clockImpls.length; i++) {
	        String packageName = clockImpls[i][1];
	        String className = clockImpls[i][2];
	        try {
	            ComponentName cn = new ComponentName(packageName, className);
	            packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
	            alarmClockIntent.setComponent(cn);
	            foundClockImpl = true;
	        } catch (NameNotFoundException e) {
	        	//no-op
	        }
	    }

	    if (foundClockImpl) {
	        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, alarmClockIntent, 0);
	        return pendingIntent;
	    }

	    return null;
	}
}