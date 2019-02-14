public class foo {
    public void printType(Type fieldType, DocErrorReporter err) {
        err.printNotice("type: " + fieldType);
        if (fieldType.asParameterizedType() != null) {
            ParameterizedType paramType = fieldType.asParameterizedType();
            err.printNotice("paramType:" + paramType);
            String qualiName = paramType.qualifiedTypeName();
            err.printNotice("qualiName: " + qualiName);

            String typeName = fieldType.asParameterizedType().typeName();
            err.printNotice("typeName: " + typeName);

            Type[] parameters = paramType.typeArguments();
            err.printNotice("parameters.length: " + parameters.length);
            for(Type p : parameters) {
                err.printNotice("param: " + p);
            }
        }
        err.printNotice("");
    }
}