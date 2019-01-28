import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Vector;

/*
 * Author: Ellina Sergeyeva
 * Date: October 23-25, 2014
 * Course: EECE 412, Assignment 4
 * Purpose: Find the password consisting of 4 digits.
 */

public class PasswordOne {

	private static String passwordStrNoSalt = "A67C8AC40D38CAE2746AA660849FAF64FB0380AF";
	private static int max = 10000;
	private static int strLenght = 4;
	private static char[] hexValues = {'0', '1', '2', '3', '4','5', '6', '7', '8', '9'};
	private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
		'B', 'C', 'D', 'E', 'F' };
	private static long estimatedTime;
	private static double estimatedTimeSec;

	public static void main (String[] args){

		Vector<String> stringsVector = new Vector<String>(); // A vector to store inputs
		Vector<String> sha1Vector = new Vector<String>(); // A vector to store inputs
		Vector<String> matchVector = new Vector<String>(); // A vector to store matches
		char[] randomChars = new char[strLenght];
		int units;
		int tens;
		int hundreds;
		int thousands;
		boolean isFound = false;

		// ****** Start measuring time *************
		long startTime = System.nanoTime();
		// *****************************************

		// ------ Generate 4 digit strings ---------//
		for(int i = 0; i < max; i++) {

			// Break down the counter (i) into components
			units = i % 10;
			tens = (i % 100) / 10;
			hundreds = (i % 1000) / 100;
			thousands = (i % 10000) / 1000;

			for( int t = 0; t < randomChars.length; t++){

				if(isFound == true){
					System.out.println("Match Found. Break the iteration.");
					break;
				}

				// Generate the four digits string
				switch(t) {
				case 0:				
					randomChars[t] = hexValues[thousands];
					break;
				case 1:
					randomChars[t] = hexValues[hundreds];
					break;				
				case 2:
					randomChars[t] = hexValues[tens];		
					break;			
				case 3:
					randomChars[t] = hexValues[units];
					break;				
				default:
					System.out.print("Reached incorrect index.");
				}
			}

			// Convert to string and add it to the vector
			String generatedStr = String.valueOf(randomChars);
			stringsVector.add(generatedStr);

			System.out.println("i: " + i + " String: " + generatedStr);

			// ------ Compute the SHA1 value ---------//

			// First, append the salt to the generated sting
			String saltedStr = "wd" + generatedStr;
			String shaStr = computeSHA1(saltedStr);
			sha1Vector.add(shaStr);

			if(passwordStrNoSalt.equals(shaStr) ){

				// ******* Stop measuring time ***************
				estimatedTime = System.nanoTime() - startTime;
				estimatedTimeSec = estimatedTime/1000000000.0;
				// *******************************************

				System.out.println("Match Found. Hash: " + shaStr + " String: " + generatedStr + " Salted: " + saltedStr);
				matchVector.add(generatedStr);
				isFound = true;
				break;
			}
			else{
				//System.out.println("Keep searching...");
			}
		}

		System.out.println("Elapsed time in ns: " + estimatedTime);
		System.out.println("Elapsed time in s: " +  estimatedTimeSec);

		/*
		// Print out the matching string
		if(matchVector.size() > 0){
			System.out.println("PRINT OUT MATCHES");
			for( int b = 0; b < matchVector.size(); b++) {
				System.out.println(matchVector.elementAt(b));
			}
			System.out.println("DONE");
		}
		*/
	}

/**
 * Purpose: Compute the SHA-1 hash value of the input string.
 * Return:  Hex string representation of the SHA-1 value.
 */
	private static String computeSHA1 (String input) {

		String sha1Str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");		
			md.reset();				
			md.update(input.getBytes("UTF-8"));
			byte[] sha1Bytes = md.digest();
			sha1Str = toHexString(sha1Bytes);
			System.out.println("SHA1: " + sha1Str);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return sha1Str;
	}

	/*
	 * Convert the byte array to hex string.
	 * Reference: http://stackoverflow.com/questions/19450452/how-to-convert-byte-array-to-hex-format-in-java
	 */
	public static String toHexString(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_CHARS[v >>> 4];
			hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
		}
		return new String(hexChars);
	}
}
