public class foo {
  String step1( String str ) {

     NewString stem = new NewString();

     if ( str.charAt( str.length()-1 ) == 's' ) {
if ( (hasSuffix( str, "sses", stem )) || (hasSuffix( str, "ies", stem)) ){
   String tmp = "";
   for (int i=0; i<str.length()-2; i++)
       tmp += str.charAt(i);
   str = tmp;
}
else {
   if ( ( str.length() == 1 ) && ( str.charAt(str.length()-1) == 's' ) ) {
      str = "";
      return str;
   }
   if ( str.charAt( str.length()-2 ) != 's' ) {
      String tmp = "";
          for (int i=0; i<str.length()-1; i++)
              tmp += str.charAt(i);
          str = tmp;
       }
    }  
 }

 if ( hasSuffix( str,"eed",stem ) ) {
   if ( measure( stem.str ) > 0 ) {
      String tmp = "";
          for (int i=0; i<str.length()-1; i++)
              tmp += str.charAt( i );
          str = tmp;
       }
 }
 else {  
    if (  (hasSuffix( str,"ed",stem )) || (hasSuffix( str,"ing",stem )) ) { 
   if (containsVowel( stem.str ))  {

      String tmp = "";
      for ( int i = 0; i < stem.str.length(); i++)
          tmp += str.charAt( i );
      str = tmp;
      if ( str.length() == 1 )
         return str;

      if ( ( hasSuffix( str,"at",stem) ) || ( hasSuffix( str,"bl",stem ) ) || ( hasSuffix( str,"iz",stem) ) ) {
         str += "e";

      }
      else {   
         int length = str.length(); 
         if ( (str.charAt(length-1) == str.charAt(length-2)) 
            && (str.charAt(length-1) != 'l') && (str.charAt(length-1) != 's') && (str.charAt(length-1) != 'z') ) {

            tmp = "";
            for (int i=0; i<str.length()-1; i++)
                tmp += str.charAt(i);
            str = tmp;
         }
         else
            if ( measure( str ) == 1 ) {
               if ( cvc(str) ) 
                  str += "e";
                }
          }
       }
    }
 }

 if ( hasSuffix(str,"y",stem) ) 
if ( containsVowel( stem.str ) ) {
   String tmp = "";
   for (int i=0; i<str.length()-1; i++ )
       tmp += str.charAt(i);
   str = tmp + "i";
        }
     return str;  
  }
}