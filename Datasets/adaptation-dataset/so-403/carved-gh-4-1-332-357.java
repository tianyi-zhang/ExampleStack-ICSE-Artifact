public class foo{
    private static List<String> readStringSubKeys
            (Preferences root, int hkey, String key)
            throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        List<String> results = new ArrayList<>();
        int[] handles = (int[]) regOpenKey.invoke(root, hkey, toCstr(key), KEY_READ
        );
        if (handles[1] != REG_SUCCESS) {
            return null;
        }
        int[] info = (int[]) regQueryInfoKey.invoke(root,
            handles[0]);

        int count = info[2]; // count  
        int maxlen = info[3]; // value length max
        for (int index = 0; index < Integer.MAX_VALUE; index++) {
            byte[] name = (byte[]) regEnumKeyEx.invoke(root, handles[0], index, maxlen + 1
            );
            if (name == null) {
                break;
            }
            results.add(new String(name).trim());
        }
        regCloseKey.invoke(root, handles[0]);
        return results;
    }
}