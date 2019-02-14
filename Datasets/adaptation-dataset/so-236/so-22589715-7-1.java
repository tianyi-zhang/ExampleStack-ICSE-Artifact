public class foo {
    private static int deleteValue(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_ALL_ACCESS});
        if (handles[1] != REG_SUCCESS) {
            return handles[1];//Can be REG_NOTFOUND, REG_ACCESSDENIED
        }
        int rc = ((Integer) regDeleteValue.invoke(root, new Object[]{handles[0], toCstr(value)}));
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return rc;
    }
}