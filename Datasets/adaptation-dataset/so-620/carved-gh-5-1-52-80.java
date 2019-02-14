public class foo{
    /**
     * Construct a persistent cookie store.
     *
     * @param context Context to attach cookie store to
     */
    public PersistentCookieStore(Context context) {
        mCookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        mCookieMap = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();

        // Load any previously stored mCookieMap into the store
        Map<String, ?> prefsMap = mCookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if (((String) entry.getValue()) != null && !((String) entry.getValue()).startsWith(COOKIE_NAME_PREFIX)) {
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    String encodedCookie = mCookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        HttpCookie decodedCookie = decodeCookie(encodedCookie);
                        if (decodedCookie != null) {
                            if (!mCookieMap.containsKey(entry.getKey()))
                                mCookieMap.put(entry.getKey(), new ConcurrentHashMap<String, HttpCookie>());
                            mCookieMap.get(entry.getKey()).put(name, decodedCookie);
                        }
                    }
                }

            }
        }
    }
}