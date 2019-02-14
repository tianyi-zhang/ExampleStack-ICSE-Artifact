public class foo {
    private static Map<String, JavaClass> collectJavaClasses(
        String jarName, JarFile jarFile) 
            throws ClassFormatException, IOException
    {
        Map<String, JavaClass> javaClasses =
            new LinkedHashMap<String, JavaClass>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry entry = entries.nextElement();
            if (!entry.getName().endsWith(".class"))
            {
                continue;
            }

            ClassParser parser = 
                new ClassParser(jarName, entry.getName());
            JavaClass javaClass = parser.parse();
            javaClasses.put(javaClass.getClassName(), javaClass);
        }
        return javaClasses;
    }
}