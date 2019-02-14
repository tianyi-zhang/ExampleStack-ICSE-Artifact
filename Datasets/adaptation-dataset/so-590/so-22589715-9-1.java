public class foo {
    private static String readString(Preferences root, int hkey, String key, String value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] handles = (int[]) regOpenKey.invoke(root, new Object[]{hkey, toCstr(key), KEY_READ});
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[]{handles[0], toCstr(value)});
        regCloseKey.invoke(root, new Object[]{handles[0]});
        return (valb != null ? new String(valb).trim() : null);
    }
}