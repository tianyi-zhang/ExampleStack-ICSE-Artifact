public class foo {
public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
    final List<Method> methods = new ArrayList<Method>();
    Class<?> klass = type;
    while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
        // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
        final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));       
        for (final Method method : allMethods) {
            if (method.isAnnotationPresent(annotation)) {
                Annotation annotInstance = method.getAnnotation(annotation);
                // TODO process annotInstance
                methods.add(method);
            }
        }
        // move to the upper class in the hierarchy in search for more methods
        klass = klass.getSuperclass();
    }
    return methods;
}
}