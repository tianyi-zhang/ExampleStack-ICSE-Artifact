package com.stackoverflow;


/**
 * License: http://creativecommons.org/licenses/by-sa/2.5/
 *
 * @see http://stackoverflow.com/questions/80476/how-to-concatenate-two-arrays-in-java
 */
public class ArrayUtils {
    public static <T> T[] concat(T[] a, T[] b) {
        final int alen = a.length;
        final int blen = b.length;
        @SuppressWarnings("unchecked")
        final T[] result = (T[]) java.lang.reflect.Array.
                newInstance(a.getClass().getComponentType(), alen + blen);
        System.arraycopy(a, 0, result, 0, alen);
        System.arraycopy(b, 0, result, alen, blen);
        return result;
    }
}
