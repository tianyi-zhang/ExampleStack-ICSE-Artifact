public class foo{
  public static Boolean getBoolean(ClassLoader cl, String key, boolean def)
      throws IllegalArgumentException {

    Boolean ret = def;

    try {
      @SuppressWarnings("rawtypes")
      Class SystemProperties = cl.loadClass("android.os.SystemProperties");

      // Parameters Types
      @SuppressWarnings("rawtypes")
      Class[] paramTypes = new Class[2];
      paramTypes[0] = String.class;
      paramTypes[1] = boolean.class;

      @SuppressWarnings("unchecked")
      Method getBoolean = SystemProperties.getMethod("getBoolean", paramTypes);

      // Parameters
      Object[] params = new Object[2];
      params[0] = new String(key);
      params[1] = Boolean.valueOf(def);

      ret = (Boolean) getBoolean.invoke(SystemProperties, params);

    } catch (IllegalArgumentException iAE) {
      throw iAE;
    } catch (Exception e) {
      Log.e(TAG, "getBoolean(context, key: " + key + ", def:" + def + ")", e);
      ret = def;
    }

    return ret;
  }
}