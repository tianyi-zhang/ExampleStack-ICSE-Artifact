public class foo{
    /**
     * Verifies that a utility class is well defined.
     * @param clazz utility class to verify.
     * @see <a href="http://stackoverflow.com/a/10872497/4271064">Stack Overflow</a>
     */
    private static void assertUtilityClassWellDefined(Class<?> clazz) throws Exception {
        Assert.assertTrue("Class must be final", Modifier.isFinal(clazz.getModifiers()));
        Assert.assertEquals(
                "Only one constructor allowed", 1,
                clazz.getDeclaredConstructors().length
        );

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            Assert.fail("Constructor is not private");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);

        for (Method method : clazz.getMethods())
            if (!Modifier.isStatic(method.getModifiers()))
                if (method.getDeclaringClass().equals(clazz))
                    Assert.fail("There exists a non-static method: " + method);
    }
}