public class foo{
  /*
   * http://stackoverflow.com/questions/4520216/how-to-add-test-coverage-to-a-private-constructor/10872497#10872497 
   */
  public static void assertUtilityClassWellDefined(final Class<?> clazz)
      throws NoSuchMethodException, InvocationTargetException,
      InstantiationException, IllegalAccessException {
    Assert.assertTrue("class must be final",
        Modifier.isFinal(clazz.getModifiers()));
    Assert.assertEquals("There must be only one constructor", 1,
        clazz.getDeclaredConstructors().length);
    final Constructor<?> constructor = clazz.getDeclaredConstructor();
    if (constructor.isAccessible() ||
        !Modifier.isPrivate(constructor.getModifiers())) {
      Assert.fail("constructor is not private");
    }
    constructor.setAccessible(true);
    constructor.newInstance();
    constructor.setAccessible(false);
    for (final Method method : clazz.getMethods()) {
      if (!Modifier.isStatic(method.getModifiers())
          && method.getDeclaringClass().equals(clazz)) {
        Assert.fail("there exists a non-static method:" + method);
      }
    }
  }
}