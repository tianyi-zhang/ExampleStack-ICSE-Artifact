public class foo{
	int measure( String stem ) {
		
		int i=0, count = 0;
		int length = stem.length();

		while ( i < length ) {
		
			for ( ; i < length ; i++ ) {
		    
				if ( i > 0 ) {
					
					if ( vowel(stem.charAt(i),stem.charAt(i-1)) )
						break;
		        }
		        else {  
		        	if ( vowel(stem.charAt(i),'a') )
		        		break; 
		       }
		   }

		   for ( i++ ; i < length ; i++ ) {
			   if ( i > 0 ) {
				   if ( !vowel(stem.charAt(i),stem.charAt(i-1)) )
					   break;
		       }
		       else {  
		    	   if ( !vowel(stem.charAt(i),'?') )
		    		   break;
		       }
		   }
		   
		   if ( i < length ) {
			   count++;
			   i++;
		   }
		} //while

		return(count);
	}
}