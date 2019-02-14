public class foo {
    /**
     * Create a key
     *
     * @param hkey HKEY_CURRENT_USER/HKEY_LOCAL_MACHINE
     * @param key
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void createKey(int hkey, String key) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        int[] ret;
        switch (hkey) {
            case HKEY_LOCAL_MACHINE:
                ret = createKey(systemRoot, hkey, key);
                regCloseKey.invoke(systemRoot, new Object[]{ret[0]});
                break;
            case HKEY_CURRENT_USER:
                ret = createKey(userRoot, hkey, key);
                regCloseKey.invoke(userRoot, new Object[]{ret[0]});
                break;
            default:
                throw new IllegalArgumentException("hkey=" + hkey);
        }

        if (ret[1] != REG_SUCCESS) {
            throw new IllegalArgumentException("rc=" + ret[1] + " key=" + key);
        }
    }
}