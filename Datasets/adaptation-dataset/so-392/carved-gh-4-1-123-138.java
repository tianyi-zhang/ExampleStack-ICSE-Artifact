public class foo{
    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);
        if (mCookieMap.containsKey(uri.getHost()) && mCookieMap.get(uri.getHost()).containsKey(name)) {
            mCookieMap.get(uri.getHost()).remove(name);
            SharedPreferences.Editor prefsWriter = mCookiePrefs.edit();
            if (mCookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(uri.getHost(), TextUtils.join(",", mCookieMap.get(uri.getHost()).keySet()));
            prefsWriter.commit();
            return true;
        } else {
            return false;
        }
    }
}