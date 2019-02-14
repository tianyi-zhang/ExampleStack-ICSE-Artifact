public class foo{
    // see: http://stackoverflow.com/questions/29797393/how-to-convert-method-getgenericreturntype-to-a-jvm-type-signature
    static void toGenericSignature(StringBuilder sb, final Type type)
    {
        if (type instanceof GenericArrayType)
        {
            sb.append("[");
            toGenericSignature(sb, ((GenericArrayType) type).getGenericComponentType());
        }
        else if (type instanceof ParameterizedType)
        {
            ParameterizedType pt = (ParameterizedType) type;
            sb.append('L');
            sb.append(((Class) pt.getRawType()).getName().replace('.', '/'));
            sb.append('<');
            for (Type p : pt.getActualTypeArguments())
            {
                toGenericSignature(sb, p);
            }
            sb.append(">;");
        }
        else if (type instanceof Class)
        {
            Class clazz = (Class) type;
            if (!clazz.isPrimitive() && !clazz.isArray())
            {
                sb.append('L');
                sb.append(clazz.getName().replace('.', '/'));
                sb.append(';');
            }
            else
            {
                sb.append(clazz.getName().replace('.', '/'));
            }
        }
        else if (type instanceof WildcardType)
        {
            WildcardType wc = (WildcardType) type;
            Type[] lowerBounds = wc.getLowerBounds();
            Type[] upperBounds = wc.getUpperBounds();
            boolean hasLower = lowerBounds != null && lowerBounds.length > 0;
            boolean hasUpper = upperBounds != null && upperBounds.length > 0;

            if (hasUpper && hasLower && Object.class.equals(lowerBounds[0]) && Object.class.equals(upperBounds[0]))
            {
                sb.append('*');
            }
            else if (hasLower)
            {
                sb.append("-");
                for (Type b : lowerBounds)
                {
                    toGenericSignature(sb, b);
                }
            }
            else if (hasUpper)
            {
                if (upperBounds.length == 1 && Object.class.equals(upperBounds[0]))
                {
                    sb.append("*");
                }
                else
                {
                    sb.append("+");
                    for (Type b : upperBounds)
                    {
                        toGenericSignature(sb, b);
                    }
                }
            }
            else
            {
                sb.append('*');
            }
        }
        else if (type instanceof TypeVariable)
        {
            // This is the other option: sb.append("T").append(((TypeVariable) type).getName()).append(";");
            // Instead replace the type variable with it's first bound.
            toGenericSignature(sb, ((TypeVariable) type).getBounds()[0]);
        }
        else
        {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}