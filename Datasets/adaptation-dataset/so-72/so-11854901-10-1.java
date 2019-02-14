public class foo {
  private static String readString(Preferences root, int hkey, String key, String value, int wow64)
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
  {
    int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
        new Integer(hkey), toCstr(key), new Integer(KEY_READ | wow64)
    });
    if (handles[1] != REG_SUCCESS) {
      return null; 
    }
    byte[] valb = (byte[]) regQueryValueEx.invoke(root, new Object[] {
        new Integer(handles[0]), toCstr(value)
    });
    regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
    return (valb != null ? new String(valb).trim() : null);
  }
}