public class foo {
  boolean cvc( String str ) {
     int length=str.length();

     if ( length < 3 )
        return false;

     if ( (!vowel(str.charAt(length-1),str.charAt(length-2)) )
        && (str.charAt(length-1) != 'w') && (str.charAt(length-1) != 'x') && (str.charAt(length-1) != 'y')
&& (vowel(str.charAt(length-2),str.charAt(length-3))) ) {

if (length == 3) {
   if (!vowel(str.charAt(0),'?')) 
              return true;
           else
              return false;
        }
        else {
           if (!vowel(str.charAt(length-3),str.charAt(length-4)) ) 
              return true; 
           else
              return false;
        } 
     }   

     return false;
  }
}