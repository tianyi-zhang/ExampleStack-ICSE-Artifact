public class foo{
    private static int deleteValue
            (Preferences root, int hkey, String key, String value)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_ALL_ACCESS);
        if (handles[1] != REG_SUCCESS) {
            return handles[1];  // can be REG_NOTFOUND, REG_ACCESSDENIED
        }
        int rc = (Integer) regDeleteValue.invoke(root,
            handles[0], toCstr(value)
        );
        regCloseKey.invoke(root, handles[0]);
        return rc;
    }
}