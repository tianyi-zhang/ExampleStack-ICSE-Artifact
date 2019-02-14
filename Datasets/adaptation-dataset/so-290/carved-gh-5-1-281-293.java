public class foo{
    // ========================================================================
    private static int deleteValue(Preferences root, int hkey, String key, String value, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_ALL_ACCESS | wow64) });
        if (handles[1] != REG_SUCCESS) {
            return handles[1]; // can be REG_NOTFOUND, REG_ACCESSDENIED
        }
        int rc = ((Integer) regDeleteValue.invoke(root, new Object[] { Integer.valueOf(handles[0]), toCstr(value) }))
                .intValue();
        regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        return rc;
    }
}