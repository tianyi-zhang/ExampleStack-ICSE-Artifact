public class foo{
    // From http://stackoverflow.com/questions/6162651/half-precision-floating-point-in-java
	// Converts integer to float ignores the higher 16 bits
	public static float toFloat(int lbits) {
		// ignores the higher 16 bits
	    int mant = lbits & 0x03ff;            // 10 bits mantissa
	    int exp = lbits & 0x7c00;             // 5 bits exponent
	   
	    if(exp == 0x7c00 ) {                  // NaN/Inf
	        exp = 0x3fc00;                    // -> NaN/Inf
	    } else if(exp != 0) {                 // normalized value	   
	        exp += 0x1c000;                   // exp - 15 + 127
	        if( mant == 0 && exp > 0x1c400)   // smooth transition
	            return Float.intBitsToFloat(
	            		( lbits & 0x8000) << 16
	                    | exp << 13 | 0x3ff);
	    } else if(mant != 0) {                // && exp==0 -> subnormal
	    	exp = 0x1c400;                    // make it normal
	        do {
	            mant <<= 1;                   // mantissa * 2
	            exp -= 0x400;                 // decrease exp by 1
	        } while((mant & 0x400) == 0);     // while not normal
	        mant &= 0x3ff;                    // discard subnormal bit
	    }                                     // else +/-0 -> +/-0
	   
	    return Float.intBitsToFloat(          // combine all parts
	        ( lbits & 0x8000 ) << 16          // sign  << ( 31 - 15 )
	        | ( exp | mant ) << 13 );         // value << ( 23 - 10 )
	}
}