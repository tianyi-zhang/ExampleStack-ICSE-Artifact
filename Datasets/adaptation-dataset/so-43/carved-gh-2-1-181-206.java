public class foo{
    /**
     * Create a key
     * 
     * @param hkey
     *            HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int[] ret;
        if (hkey == HKEY_LOCAL_MACHINE) {
            ret = createKey(systemRoot, hkey, key);
            regCloseKey.invoke(systemRoot, new Object[] { Integer.valueOf(ret[0]) });
        } else if (hkey == HKEY_CURRENT_USER) {
            ret = createKey(userRoot, hkey, key);
            regCloseKey.invoke(userRoot, new Object[] { Integer.valueOf(ret[0]) });
        } else {
            throw new IllegalArgumentException(HKEY_EQUALS + hkey);
        }
        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + "  key=" + key);
        }
    }
}