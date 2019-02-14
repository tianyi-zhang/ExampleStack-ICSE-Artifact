public class foo {
  private static void writeStringValue 
    (Preferences root, int hkey, String key, String valueName, String value) 
    throws IllegalArgumentException, IllegalAccessException,
    InvocationTargetException 
  {
    int[] handles = (int[]) regOpenKey.invoke(root, new Object[] {
        new Integer(hkey), toCstr(key), new Integer(KEY_ALL_ACCESS) });

    regSetValueEx.invoke(root,  
        new Object[] { 
          new Integer(handles[0]), toCstr(valueName), toCstr(value) 
          }); 
    regCloseKey.invoke(root, new Object[] { new Integer(handles[0]) });
  }
}