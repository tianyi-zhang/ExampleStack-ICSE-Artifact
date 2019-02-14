public class foo {
  /**
   * delete a value from a given key/value name
   * @param hkey
   * @param key
   * @param value
   * @param wow64  0 for standard registry access (32-bits for 32-bit app, 64-bits for 64-bits app)
   *               or KEY_WOW64_32KEY to force access to 32-bit registry view,
   *               or KEY_WOW64_64KEY to force access to 64-bit registry view
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   */
  public static void deleteValue(int hkey, String key, String value, int wow64) 
    throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
  {
    int rc = -1;
    if (hkey == HKEY_LOCAL_MACHINE) {
      rc = deleteValue(systemRoot, hkey, key, value, wow64);
    }
    else if (hkey == HKEY_CURRENT_USER) {
      rc = deleteValue(userRoot, hkey, key, value, wow64);
    }
    if (rc != REG_SUCCESS) {
      throw new IllegalArgumentException("rc=" + rc + "  key=" + key + "  value=" + value);
    }
  }
}