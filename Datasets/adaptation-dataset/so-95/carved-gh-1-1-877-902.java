public class foo{
	// Open a preference item directly from string name
	// Thank's so much to markus ( http://stackoverflow.com/a/4869034/1121352 )
	// FIXME: getOrder() may not work when there are PreferenceGroups
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