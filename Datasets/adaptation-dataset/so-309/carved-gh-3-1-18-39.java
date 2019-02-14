public class foo{
  /**
   * Verifies that a utility class is well defined.
   * @param clazz utility class to verify.
   */
  public static void assertUtilityClassWellDefined(final Class<?> clazz)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException {
    assertTrue("Class must be final", Modifier.isFinal(clazz.getModifiers()));
    assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);
    final Constructor<?> constructor = clazz.getDeclaredConstructor();
    if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
      Assert.fail("Constructor is not private");
    }
    constructor.setAccessible(true);
    constructor.newInstance();
    constructor.setAccessible(false);
    for (final Method method : clazz.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
        Assert.fail("There exists a non-static method: " + method);
      }
    }
  }
}