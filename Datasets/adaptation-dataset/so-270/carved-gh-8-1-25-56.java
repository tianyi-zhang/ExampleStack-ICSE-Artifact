public class foo{
  /**
   * Set an environment variable inside the JVM.
   *
   * http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java/496849#496849
   *
   * @param key   the new environment variable's identifier
   * @param value the new environment variable's value
   */
  @SuppressWarnings("unchecked")
  public void set(String key, String value) {
    if (value == null) {
      value = "";
    }

    try {
      Class<?>[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for (Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Object obj = field.get(env);
          Map<String, String> map = (Map<String, String>) obj;
          map.put(key, value);
        }
      }
    } catch (IllegalAccessException e) {
      // nothing we can do
    } catch (NoSuchFieldException e) {
      // nothing we can do
    }
  }
}