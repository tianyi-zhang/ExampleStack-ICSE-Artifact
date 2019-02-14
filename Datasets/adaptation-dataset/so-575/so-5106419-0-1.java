public class foo {
public boolean isAccessibilityEnabled(){
    int accessibilityEnabled = 0;
    final String LIGHTFLOW_ACCESSIBILITY_SERVICE = "com.example.test/com.example.text.ccessibilityService";
    boolean accessibilityFound = false;
    try {
        accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        Log.d(LOGTAG, "ACCESSIBILITY: " + accessibilityEnabled);
    } catch (SettingNotFoundException e) {
        Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
    }

    TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

    if (accessibilityEnabled==1){
        Log.d(LOGTAG, "***ACCESSIBILIY IS ENABLED***: ");


         String settingValue = Settings.Secure.getString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
         Log.d(LOGTAG, "Setting: " + settingValue);
         if (settingValue != null) {
             TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
             splitter.setString(settingValue);
             while (splitter.hasNext()) {
                 String accessabilityService = splitter.next();
                 Log.d(LOGTAG, "Setting: " + accessabilityService);
                 if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE_NAME)){
                     Log.d(LOGTAG, "We've found the correct setting - accessibility is switched on!");
                     return true;
                 }
             }
         }

        Log.d(LOGTAG, "***END***");
    }
    else{
        Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
    }
    return accessibilityFound;
}
}