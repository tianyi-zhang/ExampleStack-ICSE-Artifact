public class foo{
  /**
   * Clears an environmental variable from inside the JVM.
   *
   * @param key the environmental variable to remove
   */
  @SuppressWarnings("unchecked")
  public void unset(String key) {
    try {
      Class<?>[] classes = Collections.class.getDeclaredClasses();
      Map<String, String> env = System.getenv();
      for (Class<?> cl : classes) {
        if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
          Field field = cl.getDeclaredField("m");
          field.setAccessible(true);
          Object obj = field.get(env);
          Map<String, String> map = (Map<String, String>) obj;
          map.remove(key);
        }
      }
    } catch (IllegalAccessException e) {
      // nothing we can do
    } catch (NoSuchFieldException e) {
      // nothing we can do
    }
  }
}