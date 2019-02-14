public class foo {
    private static List<String> subKeysForPath(Preferences root, int hkey, String path)
            throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        List<String> results = new ArrayList<String>();
        int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {new Integer(hkey), toCstr(path), new Integer(KEY_READ)});
        if (handles[1] != REG_SUCCESS)
            throw new IllegalArgumentException("The system can not find the specified path: '"+getParentKey(hkey)+"\\"+path+"'");
        int[] info = (int[]) regQueryInfoKey.invoke(root, new Object[] {new Integer(handles[0])});
        int count  = info[0]; // Fix: info[2] was being used here with wrong results. Suggested by davenpcj, confirmed by Petrucio
        int maxlen = info[3]; // value length max
        for(int index=0; index<count; index++) {
            byte[] valb = (byte[]) regEnumKeyEx.invoke(root, new Object[] {new Integer(handles[0]), new Integer(index), new Integer(maxlen + 1)});
            results.add(parseValue(valb));
        }
        regCloseKey.invoke(root, new Object[] {new Integer(handles[0])});
        return results;
    }
}