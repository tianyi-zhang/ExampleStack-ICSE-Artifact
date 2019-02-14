public class foo{
    // ========================================================================
    private static List<String> readStringSubKeys(Preferences root, int hkey, String key, int wow64)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root,
            new Object[] { Integer.valueOf(hkey), toCstr(key), Integer.valueOf(KEY_READ | wow64) });
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });

        int count = info[0]; // Fix: info[2] was being used here with wrong results. Suggested by davenpcj, confirmed by
                             // Petrucio
        int maxlen = info[3]; // value length max
        for (int index = 0; index < count; index++) {
            byte[] name = (byte[]) regEnumKeyEx.invoke(root,
                new Object[] { Integer.valueOf(handles[0]), Integer.valueOf(index), Integer.valueOf(maxlen + 1) });
            String stringValue = convertByteToUTF8String(name);
            results.add(stringValue.trim());
        }
        regCloseKey.invoke(root, new Object[] { Integer.valueOf(handles[0]) });
        return results;
    }
}