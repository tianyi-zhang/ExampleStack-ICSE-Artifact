public class foo {
private PreferenceScreen findPreferenceScreenForPreference( String key, PreferenceScreen screen ) {
    if( screen == null ) {
        screen = getPreferenceScreen();
    }

    PreferenceScreen result = null;

    android.widget.Adapter ada = screen.getRootAdapter();
    for( int i = 0; i < ada.getCount(); i++ ) {
        String prefKey = ((Preference)ada.getItem(i)).getKey();
        if( prefKey != null && prefKey.equals( key ) ) {
            return screen;
        }
        if( ada.getItem(i).getClass().equals(android.preference.PreferenceScreen.class) ) {
            result = findPreferenceScreenForPreference( key, (PreferenceScreen) ada.getItem(i) );
            if( result != null ) {
                return result;
            }
        }
    }

    return null;
}
}