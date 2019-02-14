public class foo {
  private static Map<String,String> readStringValues
    (Preferences root, int hkey, String key)
    throws IllegalArgumentException, IllegalAccessException,
    InvocationTargetException 
  {
    HashMap<String, String> results = new HashMap<String,String>();
    int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
        new Integer(hkey), toCstr(key), new Integer(KEY_READ) });
    if (handles[1] != REG_SUCCESS) {
      return null;
    }
    int[] info = (int[]) regQueryInfoKey.invoke(root,
        new Object[] { new Integer(handles[0]) });

    int count = info[0]; // count  
    int maxlen = info[3]; // value length max
    for(int index=0; index<count; index++)  {
      byte[] name = (byte[]) regEnumValue.invoke(root, new Object[] {
          new Integer
            (handles[0]), new Integer(index), new Integer(maxlen + 1)});
      String value = readString(hkey, key, new String(name));
      results.put(new String(name).trim(), value);
    }
    regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
    return results;
  }
}