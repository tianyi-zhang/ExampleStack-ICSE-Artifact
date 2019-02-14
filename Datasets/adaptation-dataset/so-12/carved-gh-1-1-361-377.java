public class foo{
	String stripPrefixes ( String str) {

		String[] prefixes = { "kilo", "micro", "milli", "intra", "ultra", "mega", "nano", "pico", "pseudo"};

		int last = prefixes.length;
		for ( int i=0 ; i<last; i++ ) {
			if ( str.startsWith( prefixes[i] ) ) {
				String temp = "";
				for ( int j=0 ; j< str.length()-prefixes[i].length(); j++ )
					temp += str.charAt( j+prefixes[i].length() );
	
				return temp;
		    }
		}

		return str;
	}
}