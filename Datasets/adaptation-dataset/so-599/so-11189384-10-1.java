public class foo {
  String step4( String str ) {

     String[] suffixes = { "al", "ance", "ence", "er", "ic", "able", "ible", "ant", "ement", "ment", "ent", "sion", "tion",
                   "ou", "ism", "ate", "iti", "ous", "ive", "ize", "ise"};

     NewString stem = new NewString();

     for ( int index = 0 ; index<suffixes.length; index++ ) {
         if ( hasSuffix ( str, suffixes[index], stem ) ) {

            if ( measure ( stem.str ) > 1 ) {
               str = stem.str;
               return str;
            }
         }
     }
     return str;
  }
}