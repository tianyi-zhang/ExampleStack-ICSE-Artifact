public class foo {
public byte[] parseHexBinary(String s) {
    final int len = s.length();

    // "111" is not a valid hex encoding.
    if( len%2 != 0 )
        throw new IllegalArgumentException("hexBinary needs to be even-length: "+s);

    byte[] out = new byte[len/2];

    for( int i=0; i<len; i+=2 ) {
        int h = hexToBin(s.charAt(i  ));
        int l = hexToBin(s.charAt(i+1));
        if( h==-1 || l==-1 )
            throw new IllegalArgumentException("contains illegal character for hexBinary: "+s);

        out[i/2] = (byte)(h*16+l);
    }

    return out;
}
}