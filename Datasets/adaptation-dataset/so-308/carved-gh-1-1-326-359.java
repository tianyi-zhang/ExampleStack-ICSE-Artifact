public class foo{
	String step5( String str ) {

		if ( str.charAt(str.length()-1) == 'e' ) { 
			if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
				String tmp = "";
				for ( int i=0; i<str.length()-1; i++ ) 
					tmp += str.charAt( i );
				
				str = tmp;
			}
			else
				if ( measure(str) == 1 ) {
					String stem = "";
					for ( int i=0; i<str.length()-1; i++ ) 
						stem += str.charAt( i );
				
					if ( !cvc(stem) )
						str = stem;
				}
			}

		if ( str.length() == 1 )
			return str;
		 	if ( (str.charAt(str.length()-1) == 'l') && (str.charAt(str.length()-2) == 'l') && (measure(str) > 1) )
		 		if ( measure(str) > 1 ) {/* measure(str)==measure(stem) if ends in vowel */
		 			String tmp = "";
		 			for ( int i=0; i<str.length()-1; i++ ) 
		 				tmp += str.charAt( i );
		 			
		 			str = tmp;
		 		} 
		
		return str;
	}
}