public class foo{
	String step2( String str ) {

		String[][] suffixes = { { "ational", "ate" },
								{ "tional",  "tion" },
		                        { "enci",    "ence" },
		                        { "anci",    "ance" },
		                        { "izer",    "ize" },
		                        { "iser",    "ize" },
		                        { "abli",    "able" },
		                        { "alli",    "al" },
		                        { "entli",   "ent" },
		                        { "eli",     "e" },
		                        { "ousli",   "ous" },
		                        { "ization", "ize" },
		                        { "isation", "ize" },
		                        { "ation",   "ate" },
		                        { "ator",    "ate" },
		                        { "alism",   "al" },
		                        { "iveness", "ive" },
		                        { "fulness", "ful" },
		                        { "ousness", "ous" },
		                        { "aliti",   "al" },
		                        { "iviti",   "ive" },
		                        { "biliti",  "ble" }};
		NewString stem = new NewString();


		for ( int index = 0 ; index < suffixes.length; index++ ) {
			if ( hasSuffix ( str, suffixes[index][0], stem ) ) {
				if ( measure ( stem.str ) > 0 ) {
					str = stem.str + suffixes[index][1];
					return str;
		        }
		    }
		}

		return str;
	}
}