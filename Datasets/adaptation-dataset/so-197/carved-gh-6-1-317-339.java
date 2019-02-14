public class foo{
    // ========================================================================
    private static Map<String, String> readStringValues(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        HashMap<String, String> results = new HashMap<String, String>();
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });

        int count = info[2]; // count
        int maxlen = info[3]; // value length max
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumValue.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1) });
            String stringValue = convertByteToUTF8String(name);
            String value = readString(hkey, key, stringValue, wow64);
            results.put(stringValue.trim(), value);
        }
        regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        return results;
    }
}