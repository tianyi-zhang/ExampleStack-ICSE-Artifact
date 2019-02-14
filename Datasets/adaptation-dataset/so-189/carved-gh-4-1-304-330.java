public class foo{
    private static Map<String, String> readStringValues
            (Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        HashMap<String, String> results = new HashMap<>();
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ);
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root,
            handles[0]);

        int count = info[2]; // count  
        int maxlen = 256; // value length max
            // 256 is hardcoded value
            // initially info[3] was here
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumValue.invoke(
                    root,
                handles[0], index, maxlen + 1);
            if (name != null) {
                String value = readString(hkey, key, new String(name));
                results.put(new String(name).trim(), value);
            }
        }
        regCloseKey.invoke(root, handles[0]);
        return results;
    }
}