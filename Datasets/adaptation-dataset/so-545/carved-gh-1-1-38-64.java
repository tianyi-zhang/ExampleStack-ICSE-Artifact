public class foo{
	boolean hasSuffix( String word, String suffix, NewString stem ) {

		String tmp = "";
		
		if ( word.length() <= suffix.length() )
			return false;
		
		if (suffix.length() > 1)			
			if ( word.charAt( word.length()-2 ) != suffix.charAt( suffix.length()-2 ) )
				return false;

		stem.str = "";

		for ( int i=0; i<word.length()-suffix.length(); i++ )
			stem.str += word.charAt( i );
		    
		tmp = stem.str;

		for ( int i=0; i<suffix.length(); i++ )
			tmp += suffix.charAt( i );

		if ( tmp.compareTo( word ) == 0 )
			return true;
		else
		    return false;
		
	}
}