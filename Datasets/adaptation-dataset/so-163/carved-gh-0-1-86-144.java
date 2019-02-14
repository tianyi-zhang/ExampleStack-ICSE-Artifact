public class foo{
    public static Map<JavaClass, Set<Method>> computeReferences(
        JavaClass javaClass, Map<String, JavaClass> knownJavaClasses) 
            throws ClassNotFoundException
    {
        Map<JavaClass, Set<Method>> references = 
            new LinkedHashMap<JavaClass, Set<Method>>();
        ConstantPool cp = javaClass.getConstantPool();
        ConstantPoolGen cpg = new ConstantPoolGen(cp);
        for (Method m : javaClass.getMethods())
        {
            String fullClassName = javaClass.getClassName();
            String className = 
                fullClassName.substring(0, fullClassName.length()-6);
            MethodGen mg = new MethodGen(m, className, cpg);
            InstructionList il = mg.getInstructionList();
            if (il == null)
            {
                continue;
            }
            InstructionHandle[] ihs = il.getInstructionHandles();
            for(int i=0; i < ihs.length; i++) 
            {
                InstructionHandle ih = ihs[i];
                Instruction instruction = ih.getInstruction();
                if (!(instruction instanceof InvokeInstruction))
                {
                    continue;
                }
                InvokeInstruction ii = (InvokeInstruction)instruction;
                ReferenceType referenceType = ii.getReferenceType(cpg);
                if (!(referenceType instanceof ObjectType))
                {
                    continue;
                }

                ObjectType objectType = (ObjectType)referenceType;
                String referencedClassName = objectType.getClassName();
                JavaClass referencedJavaClass = 
                    knownJavaClasses.get(referencedClassName);
                if (referencedJavaClass == null)
                {
                    continue;
                }

                String methodName = ii.getMethodName(cpg);
                Type[] argumentTypes = ii.getArgumentTypes(cpg);
                Method method = 
                    findMethod(referencedJavaClass, methodName, argumentTypes);
                Set<Method> methods = references.get(referencedJavaClass);
                if (methods == null)
                {
                    methods = new LinkedHashSet<Method>();
                    references.put(referencedJavaClass, methods);
                }
                methods.add(method);
            }
        }
        return references;
    }
}