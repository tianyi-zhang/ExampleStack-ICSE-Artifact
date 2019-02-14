public class foo{
	String step3( String str ) {
		
		String[][] suffixes = { { "icate", "ic" },
								{ "ative", "" },
		                        { "alize", "al" },
		                        { "alise", "al" },
		                        { "iciti", "ic" },
		                        { "ical",  "ic" },
		                        { "ful",   "" },
		                        { "ness",  "" }};
		
		NewString stem = new NewString();

		for ( int index = 0 ; index<suffixes.length; index++ ) {
			if ( hasSuffix ( str, suffixes[index][0], stem ))
				if ( measure ( stem.str ) > 0 ) {
					str = stem.str + suffixes[index][1];
					return str;
				}
		    }
		return str;
	}
}