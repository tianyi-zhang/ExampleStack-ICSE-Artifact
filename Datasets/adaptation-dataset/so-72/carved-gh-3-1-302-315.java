public class foo{
    // ========================================================================
    private static String readString(Preferences root, int hkey, String key, String value, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valb = (byte[]) regQueryValueEx
                .invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(value) });
        regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        String stringValue = convertByteToUTF8String(valb);
        return (valb != null ? stringValue.trim() : null);
    }
}