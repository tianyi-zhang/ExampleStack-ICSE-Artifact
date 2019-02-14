public class foo{
    /**
     * Check whether notification listener accessibility service is enabled for the specified context and the
     * specified service class name
     * <p>
     * solution taken from http://stackoverflow.com/a/5106419/527759
     *
     * @param context
     * @param serviceClass
     * @return
     */
    public static boolean isServiceEnabled(Context context, Class<?> serviceClass) {
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE_NAME = context.getPackageName() + "/" + serviceClass.getName();
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Timber.d("isServiceEnabled: ACCESSIBILITY: " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Timber.d("isServiceEnabled: Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Timber.d("isServiceEnabled: ***ACCESSIBILIY IS ENABLED***: ");


            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Timber.d("Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    Timber.d("isServiceEnabled: Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)) {
                        Timber.d("isServiceEnabled: We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Timber.d("isServiceEnabled: ***END***");
        } else {
            Timber.d("isServiceEnabled: ***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }
}