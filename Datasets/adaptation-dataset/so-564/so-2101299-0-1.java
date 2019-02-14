public class foo {
public static int intValueOf( String str )
{
    int ival = 0, idx = 0, end;
    boolean sign = false;
    char ch;

    if( str == null || ( end = str.length() ) == 0 ||
       ( ( ch = str.charAt( 0 ) ) < '0' || ch > '9' )
          && ( !( sign = ch == '-' ) || ++idx == end || ( ( ch = str.charAt( idx ) ) < '0' || ch > '9' ) ) )
        throw new NumberFormatException( str );

    for(;; ival *= 10 )
    {
        ival += '0'- ch;
        if( ++idx == end )
            return sign ? ival : -ival;
        if( ( ch = str.charAt( idx ) ) < '0' || ch > '9' )
            throw new NumberFormatException( str );
    }
}
}