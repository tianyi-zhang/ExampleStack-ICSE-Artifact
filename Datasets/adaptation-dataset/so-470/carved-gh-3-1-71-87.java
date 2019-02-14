public class foo{
    // Taken from
    // http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public static byte[] decode( final String encoded) {
        if( (encoded.length() % 2) != 0) {
            throw new IllegalArgumentException(
                    "Input string must contain an even number of characters");
        }

        final byte result[] = new byte[encoded.length() / 2];
        final char enc[] = encoded.toCharArray();
        for( int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder( 2);
            curr.append( enc[i]).append( enc[i + 1]);
            result[i / 2] = (byte) Integer.parseInt( curr.toString(), 16);
        }
        return result;
    }
}