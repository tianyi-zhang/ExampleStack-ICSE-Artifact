public class foo{
    @Override
    public void add(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);
        // Save cookie into local store, or remove if expired
        if (!cookie.hasExpired()) {
            if (!mCookieMap.containsKey(uri.getHost()))
                mCookieMap.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            mCookieMap.get(uri.getHost()).put(name, cookie);
        } else {
            if (mCookieMap.containsKey(uri.toString()))
                mCookieMap.get(uri.getHost()).remove(name);
        }
        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
        prefsWriter.putString(uri.getHost(), TextUtils.join(",", mCookieMap.get(uri.getHost()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.commit();
    }
}