public class foo{
    // ========================================================================
    private static void writeStringValue(Preferences root, int hkey, String key, String valueName, String value,
        int wow64) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        regSetValueEx.invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(valueName), toCstr(value) });
        regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
    }
}