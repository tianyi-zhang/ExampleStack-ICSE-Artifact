public class foo {
public static String convertToStringRepresentation(final long value){
    final long[] dividers = new long[] { T, G, M, K, 1 };
    final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
    if(value < 1)
        throw new IllegalArgumentException("Invalid file size: " + value);
    String result = null;
    for(int i = 0; i < dividers.length; i++){
        final long divider = dividers[i];
        if(value >= divider){
            result = format(value, divider, units[i]);
            break;
        }
    }
    return result;
}
}