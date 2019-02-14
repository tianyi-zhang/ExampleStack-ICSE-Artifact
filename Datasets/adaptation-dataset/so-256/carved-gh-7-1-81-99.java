public class foo{
    protected void add(HttpUrl uri, Cookie cookie) {
        String name = getCookieToken(cookie);

        // Save cookie into local store, or remove if expired
        if (!cookie.persistent()) {
            if (!cookies.containsKey(uri.host()))
                cookies.put(uri.host(), new ConcurrentHashMap<String, Cookie>());
            cookies.get(uri.host()).put(name, cookie);
        } else {
            if (cookies.containsKey(uri.toString()))
                cookies.get(uri.host()).remove(name);
        }

        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(uri.host(), TextUtils.join(",", cookies.get(uri.host()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.commit();
    }
}