public class foo{
    protected static void findReferences(String jarName, JarFile jarFile) 
        throws ClassFormatException, IOException, ClassNotFoundException
    {
        Map<String, JavaClass> javaClasses = 
            collectJavaClasses(jarName, jarFile);

        for (JavaClass javaClass : javaClasses.values())
        {
            System.out.println("Class "+javaClass.getClassName());
            Map<JavaClass, Set<Method>> references = 
                computeReferences(javaClass, javaClasses);
            for (Entry<JavaClass, Set<Method>> entry : references.entrySet())
            {
                JavaClass referencedJavaClass = entry.getKey();
                Set<Method> methods = entry.getValue();
                System.out.println(
                    "    is referencing class "+
                    referencedJavaClass.getClassName()+" by calling");
                for (Method method : methods)
                {
                    System.out.println(
                        "        "+method.getName()+" with arguments "+
                        Arrays.toString(method.getArgumentTypes()));
                }
            }
        }
    }
}