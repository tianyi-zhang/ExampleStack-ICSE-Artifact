public class foo{
  // parts of this are from:
  // http://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
  public static void setEnv(Map<String, String> newenv) throws Exception {
    Map<String, String> env = System.getenv();
    Class[] classes = Collections.class.getDeclaredClasses();
    for (Class cl : classes) {
      if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
        Field field = cl.getDeclaredField("m");
        field.setAccessible(true);
        Object obj = field.get(env);
        Map<String, String> map = (Map<String, String>) obj;
        map.clear();
        map.putAll(newenv);
      }
    }
  }
}