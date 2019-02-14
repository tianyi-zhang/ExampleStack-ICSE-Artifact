/*******************************************************************************
 * gMix open source project - https://svs.informatik.uni-hamburg.de/gmix/
 * Copyright (C) 2014  SVS
 *
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License 
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package staticContent.framework.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.channels.Channels;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import staticContent.framework.message.Reply;
import staticContent.framework.message.Request;


public final class Util {
	
	
	public static final int NOT_SET =-222222222;
	public static final short NOT_SETs = -2222;
	private static SecureRandom random = new SecureRandom();
	
	
	/**
	 * Empty constructor. Never used since all methods are static.
	 */
	private Util() {
		
	}
	
	
	/**
	 * Converts the bypassed long value to a byte array.
	 * 
	 * @param source	The long value to be translated.
	 * @return 			Byte array representation of the bypassed long value.
	 */
	public static byte[] longToByteArray(long source) {
		
		byte[] result = new byte[8];
		
		for (int i=0; i<8; i++) {
			
			result[i] = new Long((source >> (i << 3)) & 255L).byteValue();
			
		}
		
		return result;
	}
	
	
	/**
	 * Converts the bypassed byte array to a long value.
	 * 
	 * @param byteArray	The byte array to be translated.
	 * @return 			"long" representation of the bypassed byte array.
	 */
	public static long byteArrayToLong(byte[] byteArray) {

		long result = 0;
		
		for (int i=0; (i<byteArray.length) && (i<8); i++) {
			
			result |= ((((long) byteArray[i]) & 255L) << (i << 3));
			
		}
		
		return result;
	}
	
	
	/**
	 * Converts the bypassed int value to a byte array.
	 * 
	 * @param source	The int value to be translated.
	 * @return 			Byte array representation of the bypassed int value.
	 */
	public static byte[] intToByteArray(int source) {
		
		byte[] result = new byte[4];
		
		for (int i = 0; i < 4; ++i) {
			
			result[3-i] = (byte)((source & (0xff << (i << 3))) >>> (i << 3));
			
		}
		
		return result;
	}
	
	
	/**
	 * Converts the bypassed byte array to an int value.
	 * 
	 * @param byteArray	The byte array to be translated.
	 * @return 			"int" representation of the bypassed byte array.
	 */
	public static int byteArrayToInt(byte[] byteArray) {
		
		int result = 0;
	
		for (int i = 0; (i<byteArray.length) && (i<8); i++) {
			
			result |= (byteArray[3-i] & 0xff) << (i << 3);
			
		}
		
		return result;
					
	}	
	
	
	/**
	 * Converts the bypassed short value to a byte array.
	 * 
	 * @param source	The short value to be translated.
	 * @return 			Byte array representation of the bypassed short value.
	 */
	public static byte[] shortToByteArray(int source) {
		
		byte[] result = new byte[2];
		
		result[0] = (byte)((source & 0xFF00) >> 8);
		result[1] = (byte)(source & 0x00FF);
		
		return result;

	}
	
	
	/**
	 * Converts the bypassed byte array to a short value.
	 * 
	 * @param byteArray	The byte array to be translated.
	 * @return 			"short" representation of the bypassed byte array.
	 */
	public static short byteArrayToShort(byte[] byteArray) {
		
		short result = 0;
		
		result |= (byteArray[0] & 0xFF);
		result <<= 8;
		result |= (byteArray[1] & 0xFF);
		
		return result;
		
	}
	
	
	
	public static byte[] floatToByteArray(float source) {
		return intToByteArray(Float.floatToRawIntBits(source));
	}
	
	
	public static float byteArrayToFloat(byte[] byteArray) {
		return Float.intBitsToFloat(byteArrayToInt(byteArray));		
	}
	
	
	public static byte[] doubleToByteArray(double source) {
		return longToByteArray(Double.doubleToRawLongBits(source));
	}
	
	
	public static double byteArrayToDouble(byte[] byteArray) {
		return Double.longBitsToDouble(byteArrayToLong(byteArray));	
	}
	
	
	public static byte[] charToByteArray(char value) {       
    	return shortToByteArray(value);
    }
	
	
	public static char byteArrayToChar(byte[] byteArray) {
		return (char) byteArrayToShort(byteArray);
	}
	

	public static byte[] concatArrays(byte[][] arrays) {
		
		byte[] result = concatArrays(arrays[0], arrays[1]);
		
		for (int i=2; i<arrays.length; i++)
			result = concatArrays(result, arrays[i]);

		return result;
		
	}
	
	
	public static byte[] concatArrays(byte[] firstArray, byte[] secondArray) {
		
		byte[] result = new byte[firstArray.length + secondArray.length];
		
		System.arraycopy(firstArray, 0, result, 0, firstArray.length);
		
		System.arraycopy(	secondArray,
						 	0, result, 
						 	firstArray.length, 
						 	secondArray.length
						 	);
		
		return result;
		
	}
	
	
	public static double[] concatArrays(double[] firstArray, double[] secondArray) {

		double[] result = new double[firstArray.length + secondArray.length];

		System.arraycopy(firstArray, 0, result, 0, firstArray.length);

		System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);

		return result;

	}
	
	
	public static byte[] removePartOfArray(byte[] data, int offset, int length) {
		
		byte[] result = new byte[data.length - length];
		
		if (offset != 0) // add first part
			System.arraycopy(data, 0, result, 0, offset);
		

		// add second part
		System.arraycopy(data, offset+length, result, offset, data.length - (offset+length));
		
		return result;

	}
	
	
	public static byte[] xor(byte[] x, byte[] y) {
		 
		 assert (x.length == y.length);
		 
		 byte[] xored = new byte[x.length];
		 
		 for(int i=0; i<xored.length;i++) {
			 xored[i]= (byte) (x[i] ^ y[i]);
		 }
		 
		 return xored;
	 }
	
	
	public static void writeInt(OutputStream outputStream, int integerToWrite) throws IOException {
		outputStream.write(intToByteArray(integerToWrite));
	}
	
	
	public static void writeLong(OutputStream outputStream, long longToWrite) throws IOException {
		outputStream.write(longToByteArray(longToWrite));
	}
	
	
	public static void writeShort(OutputStream outputStream, short shortToWrite) throws IOException {
		outputStream.write(shortToByteArray(shortToWrite));
	}
	
	
	public static byte[] forceRead(InputStream inputStream, byte[] result) throws IOException {
		int bytesRead = 0;
		int total = result.length;
		int remaining = total;
		int read;
		while (remaining > 0) {
			read = inputStream.read(result, bytesRead, remaining);
			if (read == -1) // EOF
				return null;
			remaining -= read;
			bytesRead += read;
		}
		return result;
	}
	
	
	public static byte[] forceRead(InputStream inputStream, int length) throws IOException {
		int bytesRead = 0;
		byte[] result = new byte[length];
		int total = result.length;
		int remaining = total;
		int read;
		while (remaining > 0) {
			read = inputStream.read(result, bytesRead, remaining);
			if (read == -1) // EOF
				return null;
			remaining -= read;
			bytesRead += read;
		}
		return result;
	}
	
	
	public static int forceReadInt(InputStream inputStream) throws IOException {
		int bytesRead = 0;
		byte[] result = new byte[4];
		int total = result.length;
		int remaining = total;
		int read;
		while (remaining > 0) {
			read = inputStream.read(result, bytesRead, remaining);
			remaining -= read;
			bytesRead += read;
		}
		return byteArrayToInt(result);
	}
	
	
	public static long forceReadLong(InputStream inputStream) throws IOException {
		int bytesRead = 0;
		byte[] result = new byte[8];
		int total = result.length;
		int remaining = total;
		int read;
		while (remaining > 0) {
			read = inputStream.read(result, bytesRead, remaining);
			remaining -= read;
			bytesRead += read;
		}
		return byteArrayToLong(result);
	}
	
	
	public static short forceReadShort(InputStream inputStream) throws IOException {
		int bytesRead = 0;
		byte[] result = new byte[2];
		int total = result.length;
		int remaining = total;
		int read;
		while (remaining > 0) {
			read = inputStream.read(result, bytesRead, remaining);
			remaining -= read;
			bytesRead += read;
		}
		return byteArrayToShort(result);
	}
	
	
	public static byte[] generateMD5Hash(byte[] data) {
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(data);
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
		
	}
	
	
	public static String display(byte[] data) {
		return "[" +getStringWithoutNewLines(data) +"]";
	}
	
	
	public static String md5(byte[] data) {
		return "[" +asAscii(new String(generateMD5Hash(data))) +"]";
	}
	
	
	public static String getStringWithoutNewLines(byte[] data) {
		return ((new String(data)).replaceAll("\n", " ")).replaceAll("\r", " ");
	}
	
	
	public static String asAscii(String s) {
		String result = "";
		for (int i=0;i<s.length();i++) {
			result += (int)s.charAt(i);// +" ";
		}
		return result;
	}
	
	
	/*public static void checkIfBCIsInstalled() {
		
		// check if bouncy castle is installed
		try {
			
			Security.addProvider(new BouncyCastleProvider());
			KeyGenerator keyGen = KeyGenerator.getInstance("AES", "BC");
		    keyGen.init(new SecureRandom());
		    Key key = keyGen.generateKey();
		    Cipher encrypt = Cipher.getInstance("AES/OFB/NOPADDING", "BC");
		    encrypt.init(Cipher.ENCRYPT_MODE, key);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			CipherOutputStream cos = new CipherOutputStream(baos, encrypt);
			cos.write("plaintext".getBytes());
			cos.close();
			
		} catch (InvalidKeyException e) {
			System.err.println(
					"The \"Java Cryptography Extension (JCE) Unlimited " +
					"Strength Jurisdiction Policy Files\" seem to be not " +
					"installed but are required. Download available at " +
					"http://www.oracle.com/technetwork/java/javase/" +
					"downloads/index.html\n\n"
					+e.getMessage()
					);
			
		    e.printStackTrace();
		    System.exit(1);
			
		} catch (Exception e) {
			
			System.err.println(
					"The Bouncy Castle crypto provider (http://www.bou"+
					"ncycastle.org/) seems to be not installed or " +
					"working! \n" +
					"Please add the Bouncy Castle jar-file to your " +
					"classpath.\n"
					+e.getMessage()
					);
			
		    e.printStackTrace();
		    System.exit(1);
		    
		}
		
		// check if the Java Cryptography Extension (JCE) Unlimited Strength 
		// Jurisdiction Policy Files are installed
		try {
	    	KeyPairGenerator kpg =  KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
		    kpg.generateKeyPair();
	    } catch (NoSuchAlgorithmException e) {
	    	
	    	System.err.println(	
	    			"The Java Cryptography Extension (JCE) Unlimited" +
	    			" Strength Jurisdiction Policy Files (http://www." +
	    			"oracle.com/technetwork/java/javase/downloads/" +
	    			"index.html) seem to be not installed or " +
					"working! \n"
	    			+e.getMessage()
					);
	
	    	e.printStackTrace();
	    	System.exit(1);
	    	
	    }
	}*/

	// http://stackoverflow.com/questions/1179672/unlimited-strength-jce-policy-files
	public static void removeCryptographyRestrictions() {
		Security.addProvider(new BouncyCastleProvider());
	    if (!isRestrictedCryptography()) {
	        return;
	    }
	    try {
	        /*
	         * Do the following, but with reflection to bypass access checks:
	         *
	         * JceSecurity.isRestricted = false;
	         * JceSecurity.defaultPolicy.perms.clear();
	         * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
	         */
	        final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
	        final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
	        final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

	        final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
	        isRestrictedField.setAccessible(true);
	        isRestrictedField.set(null, false);

	        final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
	        defaultPolicyField.setAccessible(true);
	        final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

	        final Field perms = cryptoPermissions.getDeclaredField("perms");
	        perms.setAccessible(true);
	        ((Map<?, ?>) perms.get(defaultPolicy)).clear();

	        final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
	        instance.setAccessible(true);
	        defaultPolicy.add((Permission) instance.get(null));
	    } catch (final Exception e) {
	    	throw new RuntimeException("could not remove cryptography restrictions; " 
	    			+"try to install the \"Java Cryptography Extension (JCE) Unlimited " +
					"Strength Jurisdiction Policy Files\". Download available at " +
					"http://www.oracle.com/technetwork/java/javase/downloads/index.html\n\n"
					+"\ndetailed error message: " +e.getMessage());
	    }
	}

	private static boolean isRestrictedCryptography() {
	    // This simply matches the Oracle JRE, but not OpenJDK.
	    return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
	}

	public static boolean assertionsEnabled() {
		
		boolean enabled = false;
		assert enabled = true;
		
		if (!enabled)
			return false;
		else
			return true;
	}
	
	
	public static String getFileContent(String fileNameOrPath) {
		String result = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
				new DataInputStream(new FileInputStream(fileNameOrPath))));
			String line;
			while ((line = br.readLine()) != null)
				result += line + "\n";
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(	"ERROR: could not read from file " 
										+fileNameOrPath +"!");
		}
		return result;
	}
	
	
	public static void writeToFile(String content, String fileNameOrPath) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new DataOutputStream(new FileOutputStream(fileNameOrPath))));
			bw.write(content);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(	"ERROR: could not write to file " 
										+fileNameOrPath +"!");
		}
	}
	
	
	public static String[] getTextBetweenAllAAndB(String source, String a, String b) {
		ArrayList<String> result = new ArrayList<String>();
		String[] lines = source.split("\n");
		for (String line:lines) {
			int positionA = source.indexOf(a);
			int positionB = source.indexOf(b, positionA);
			if (positionA != -1 && positionB != -1) {
				result.add(line.substring(positionA + a.length(), positionB));
			}
		}
		return result.toArray(new String[0]);
	}
	
	
	public static String getTextBetweenAAndB(String source, String a, String b) {
		int positionA = source.indexOf(a);
		int positionB = source.indexOf(b, positionA);
		return source.substring(positionA + a.length(), positionB);
	}
	
	
	public static int getIthIndexOf(char searchFor, String searchIn, int i) {
		int ctr = 0;
		for (int j=0; j<searchIn.length(); j++)
			if (searchIn.charAt(j) == searchFor)
				if (++ctr == i)
					return j;
		return -1;
	}
	
	
	/*public static int getIthIndexOf(char searchFor, String searchIn, int i, int startIndex) {
		searchIn = searchIn.substring(startIndex);
		int ctr = 0;
		for (int j=0; j<searchIn.length(); j++)
			if (searchIn.charAt(j) == searchFor)
				if (++ctr == i)
					return j;
		return -1;
	}*/
	
	
	public static String[][] splitInChunks(int chunkSize, String[] source) {
		if (source.length <= chunkSize)
			return new String[][] {source};
		int chunks = (int) Math.ceil((double)source.length / (double)chunkSize);
		String[][] result = new String[chunks][];
		int pointer = 0;
		for (int i=0; i<result.length; i++)
			if (i < result.length-1)
				result[i] = Arrays.copyOfRange(source, pointer, pointer+=chunkSize);
			else
				result[i] = Arrays.copyOfRange(source, pointer, source.length);
		return result;
	}
	
	
	
	public static Request[][] splitInChunks(int chunkSize, Request[] source) {
		if (source.length <= chunkSize)
			throw new RuntimeException("cannot split the bypassed array (array is smaller than chunkSize)"); 
		int chunks = (int) Math.ceil((double)source.length / (double)chunkSize);
		Request[][] result = new Request[chunks][];
		int pointer = 0;
		for (int i=0; i<result.length; i++)
			if (i < result.length-1)
				result[i] = Arrays.copyOfRange(source, pointer, pointer+=chunkSize);
			else
				result[i] = Arrays.copyOfRange(source, pointer, source.length);
		return result;
	}
	
	
	public static Reply[][] splitInChunks(int chunkSize, Reply[] source) {
		if (source.length <= chunkSize)
			throw new RuntimeException("cannot split the bypassed array (array is smaller than chunkSize)"); 
		int chunks = (int) Math.ceil((double)source.length / (double)chunkSize);
		Reply[][] result = new Reply[chunks][];
		int pointer = 0;
		for (int i=0; i<result.length; i++)
			if (i < result.length-1)
				result[i] = Arrays.copyOfRange(source, pointer, pointer+=chunkSize);
			else
				result[i] = Arrays.copyOfRange(source, pointer, source.length);
		return result;
	}
	
	
	
	/*@SuppressWarnings("unchecked") // type safety is assured through generic method header; the need for "@SuppressWarnings" is a flaw/feature of java generics...
	public static <T> T[][] splitInChunks(int chunkSize, T[] source) {
		if (source.length <= chunkSize)
			throw new RuntimeException("cannot split the bypassed array (array is smaller than chunkSize)"); 
		int chunks = (int) Math.ceil((double)source.length / (double)chunkSize);
		Object[][] result = new Object[chunks][];
		int pointer = 0;
		for (int i=0; i<result.length; i++)
			if (i < result.length-1)
				result[i] = Arrays.copyOfRange(source, pointer, pointer+=chunkSize);
			else
				result[i] = Arrays.copyOfRange(source, pointer, source.length);
		return (T[][]) result;
	}*/

	
	public static byte[][] splitInChunks(int chunkSize, byte[] source) {
		if (source.length <= chunkSize)
			throw new RuntimeException("cannot split the bypassed array (array is smaller than chunkSize)"); 
		int chunks = (int) Math.ceil((double)source.length / (double)chunkSize);
		byte[][] result = new byte[chunks][];
		int pointer = 0;
		for (int i=0; i<result.length; i++)
			if (i < result.length-1)
				result[i] = Arrays.copyOfRange(source, pointer, pointer+=chunkSize);
			else
				result[i] = Arrays.copyOfRange(source, pointer, source.length);
		return result;
	}
	
	
	
	/*@SuppressWarnings("unchecked") // type safety is assured through generic method header; the need for "@SuppressWarnings" is a flaw/feature of java generics...
	public static <T> T[][] splitAfter(int splitBefore, T[] source) {
		if (source.length <= splitBefore)
			throw new RuntimeException("cannot split the bypassed array (array too small)"); 
		Object[][] result = new Object[2][];
		result[0] = Arrays.copyOfRange(source, 0, splitBefore);
		result[1] = Arrays.copyOfRange(source, splitBefore, source.length);
		return (T[][]) result;
	}*/
	
	
	public static byte[][] split(int splitBefore, byte[] source) {
		if (source.length <= splitBefore)
			throw new RuntimeException("cannot split the bypassed array (array too small: "+source.length +"<=" +splitBefore +")"); 
		byte[][] result = new byte[2][];
		result[0] = Arrays.copyOfRange(source, 0, splitBefore);
		result[1] = Arrays.copyOfRange(source, splitBefore, source.length);
		return result;
	}
	
	
	private static final long SLEEP_PRECISION = TimeUnit.MILLISECONDS.toNanos(2); // TODO: determine for current machine
	
	public static void sleepNanos(long nanoDuration) throws InterruptedException {
		// see http://andy-malakov.blogspot.de/2010/06/alternative-to-threadsleep.html
		final long end = System.nanoTime() + nanoDuration;
		long timeLeft = nanoDuration;
		do {
			if (timeLeft > SLEEP_PRECISION)
				Thread.sleep(1);
			else
				Thread.yield();
			timeLeft = end - System.nanoTime();
			if (Thread.interrupted())
				throw new InterruptedException();
		} while (timeLeft > 0);
	}
	
	
	public static String humanReadableByteCount(long bytes, boolean si) {
		// see http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	

	/*public static void main(String[] args) {
		for (File f:getFilePaths(".", "StaticFinctionSettings.txt"))
			System.out.println(f); 
		
	} */
	
	
	// example:
	// for (File f:getFilePaths(".", "StaticFinctionSettings.txt"))
	//      System.out.println(f); 
	public static File[] getFilePaths(String rootFolder, String filename) {
		Vector<File> found = new Vector<File>();
		File root = new File(rootFolder);
		getFilePaths(root, filename, found);
		return found.toArray(new File[0]);
	}
	
	
	private static void getFilePaths(File rootFolder, String filename, Vector<File> found) {
		if (rootFolder.isDirectory()) {
			File[] files = rootFolder.listFiles();
			for (File f : files)
				getFilePaths(f, filename, found);
		} else if (rootFolder.isFile() && rootFolder.getName().equalsIgnoreCase(filename)) {
			found.add(rootFolder);
		}
	}
	
	
	public static boolean contains(int searchFor, int[] in) {
		for (int i=0; i<in.length; i++)
			if (in[i] == searchFor)
				return true;
		return false;
	}
	
	
	public static int getRandomInt(int min, int max) {
		return min + (int)(random.nextDouble()*(max-min)+1);
	}
	
	
	public static int getRandomInt(int min, int max, Random random) {
		return min + (int)(random.nextDouble()*(max-min)+1);
	}
	
	
	public static short unsignedByteToShort(byte source) {
		return (short) (0x000000FF & ((int)source));
    }

	
	public static int unsignedShortToInt(byte[] source) {
	    
		return ((int)((0x00FF & source[0]) << 8 
				| (0x00FF & source[1])) & 0xFFFF
				);

    }
	
	
	public static long unsignedIntToLong(byte[] source) {
	    
		return ((long)((0x000000FF & source[0]) << 24 
				| ((0x000000FF & source[1]) << 16)
				| ((0x000000FF & source[2]) << 8)
				| (0x000000FF & source[3])) & 0xFFFFFFFFL
				);

    }
	
	
	public static BigInteger unsignedLongToBigInteger(byte[] source) {
	    
		byte[] result = new byte[source.length+1];
		result[0] = 0;
		
		for (int i=1; i<result.length; i++)
			result[i] = source[i-1];
        
		return new BigInteger(result);

    }


	public static byte[] signedLongToUnsignedLong(long signedLong) {
		
	    if (signedLong < 0)
	    	throw new ArithmeticException("a signed long < 0 can't be converted to an unsigned long");

		return longToByteArray(signedLong);

    }
	
	
	public static byte[] signedIntToUnsignedInt(int signedInt) {
		
	    if (signedInt < 0)
	    	throw new ArithmeticException("a signed int < 0 can't be converted to an unsigned int");

		return intToByteArray(signedInt);

    }


	public static byte[] signedShortToUnsignedShort(short signedShort) {
		
	    if (signedShort < 0)
	    	throw new ArithmeticException("a signed short < 0 can't be converted to an unsigned short");

		return shortToByteArray(signedShort);

    }
	
	
	public static String toHex(byte[] bytes) {
	    BigInteger b = new BigInteger(1, bytes);
	    return String.format("%0" + (bytes.length << 1) + "X", b);
	}
	
	
	public static String toBinary(byte[] bytes) {
	    BigInteger b = new BigInteger(1, bytes);
	    return b.toString(2);
	}

	
	/**
	 * for byte b = "00000001 base 2", result[0] will be 1 (true) and result[7] will be 0 (false)
	 * 
	 * boolean[] arr = byteToBooleanArray((byte)1); // binary: 00000001
	 * System.out.println(arr[0]); // will display "true" 
	 * System.out.println(arr[1]); // will display "false"
	 */
	public static boolean[] byteToBooleanArray(byte b) {
		boolean[] result = new boolean[8];
		for (int i=0; i<result.length; i++)
			result[i] = (b & (1 << i)) != 0;
		return result;
	}
	
	
	/**
	 * for byte b = "00000001 base 2", result[0] will be 0 (false) and result[7] will be 1 (true)
	 * 
	 * boolean[] arr = byteToReverseBooleanArray((byte)1); // binary: 00000001
	 * System.out.println(arr[0]); // will display "false" 
	 * System.out.println(arr[7]); // will display "true"
	 */
	public static boolean[] byteToReverseBooleanArray(byte b) {
		boolean[] result = new boolean[8];
		for (int i=0; i<result.length; i++)
			result[result.length-1-i] = (b & (1 << i)) != 0;
		return result;
	}
	
	

	/**
	 * bitIndex from right to left (0 = least significant = rightmost bit)
	 */
	public static boolean getBitAt(int bitIndex, byte b) {
		return ((int)b & (1 << (bitIndex & 31))) != 0;
	}
	
	
	/**
	 * bitIndex from right to left (0 = least significant = rightmost bit)
	 */
	public static byte setBitAt(int bitIndex, boolean value, byte b) {
		if (value)
			return (byte) ((int)b | (1 << (bitIndex & 31)));
		else 
			return (byte) ((int)b & ~(1 << (bitIndex & 31)));
	}
	
	
	/**
	 * bitIndex from right to left (0 = least significant = rightmost bit)
	 */
	public static byte flipBitAt(int bitIndex, byte b) {
		return (byte) ((int)b ^ (1 << (bitIndex & 31)));
	}
	
	
	/**
	 * bitIndex from right to left (0 = least significant = rightmost bit)
	 */
	public static byte reverseByte(byte b) {
		byte result = 0;
		for (int i=0; i<8; i++) {
			result = setBitAt(i, getBitAt(i, b), result);
		}
		return result;
	}
	
	/**
	 * will modify the bypassed array!
	 * @param array
	 * @return
	 */
	public static byte[] reverse(byte[] array) {
		for (int i=0; i<array.length/2; i++) {
			byte tmp = array[i];
			array[i] = array[array.length-1-i];
			array[array.length-1-i] = tmp;
		}
		return array;
	}

	
	
	public static InputStream tryDetectCompressionMethod(String pathToFile) throws FileNotFoundException {
		// gzip:
		boolean isGzip = true;
		try {
			InputStream is = new FileInputStream(pathToFile);
			is = new GZIPInputStream(is);
			is.read(new byte[10]);
		} catch (IOException e) {
			isGzip = false;
		}
		if (isGzip) {
			System.out.println("detected gzip compression"); 
			try {
				return new GZIPInputStream(new FileInputStream(pathToFile));
			} catch (IOException e) {
				e.printStackTrace();
				try {Thread.sleep(1000);} catch (InterruptedException e1) {}
				return tryDetectCompressionMethod(pathToFile);
			}
		}
		// zip:
		boolean isZip = true;
		try {
			InputStream is = new FileInputStream(pathToFile);
			is = new ZipInputStream(is);
			int len = is.read(new byte[10]);
			if (len == -1) // ZipInputStream accepts uncompressed streams but than can't read it...
				isZip = false;
		} catch (IOException e) {
			isZip = false;
		}
		if (isZip) {
			System.out.println("detected zip compression"); 
			try {
				return new ZipInputStream(new FileInputStream(pathToFile));
			} catch (IOException e) {
				e.printStackTrace();
				try {Thread.sleep(1000);} catch (InterruptedException e1) {}
				return tryDetectCompressionMethod(pathToFile);
			}
		}
		// assume raw file:
		System.out.println("detected no compression"); 
		try {
			return new FileInputStream(pathToFile);
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				throw (FileNotFoundException) e;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
			return tryDetectCompressionMethod(pathToFile);
		}
	}
	
	
	public static String removeFileExtension(String fileNameOrPath) {
		int suffixStart = fileNameOrPath.lastIndexOf(".");
		if (suffixStart != -1) { 
			fileNameOrPath = fileNameOrPath.substring(0, suffixStart);
			removeFileExtension(fileNameOrPath);
		}
		return fileNameOrPath;
	}
	
	public static String removeLineBreakAtEnd(String string) {
		if (string.endsWith("\r\n"))
			return string.substring(0, string.length() - 2);
		else if (string.endsWith("\n"))
			return string.substring(0, string.length() - 1);
		else if (string.endsWith("\r"))
			return string.substring(0, string.length() - 1);
		else 
			return string;
	}
	
	
	private static boolean warningDisplayed = false;
	
	
	public static synchronized void displayWarningOnLowReservedMemory() {
		if (warningDisplayed)
			return;
		warningDisplayed = true;
		long maxMem = Runtime.getRuntime().maxMemory();
		if (maxMem < 500000000)
			System.err.println("WARNING: this program may be slow as only " +Util.humanReadableByteCount(maxMem, false) +" RAM are allocated. Run with -Xms<size> -Xmx<size> to associate more RAM (Java heap size). E.g. run with \"-Xms1024m -Xmx1024m\" to associate 1024 MB."); 
	}
	
	
	public static int[] toIntArray(Vector<Integer> source) {
		int[] result = new int[source.size()];
		for (int i=0; i<result.length; i++)
			result[i] = source.get(i);
		return result;
	}
	
	
	public static long[] toLongArray(Vector<Long> source) {
		long[] result = new long[source.size()];
		for (int i=0; i<result.length; i++)
			result[i] = source.get(i);
		return result;
	}
	
	
	public static float[] toFloatArray(Vector<Float> source) {
		float[] result = new float[source.size()];
		for (int i=0; i<result.length; i++)
			result[i] = source.get(i);
		return result;
	}
	
	
	// see http://stackoverflow.com/questions/686231/java-quickly-read-the-last-line-of-a-text-file
	public static String readLastLine(String fileName) {
	    try {
	    	File file = new File(fileName);
	        RandomAccessFile fileHandler = new RandomAccessFile(file, "r");
	        long fileLength = file.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        for(long filePointer=fileLength; filePointer!=-1; filePointer--) {
	            fileHandler.seek(filePointer);
	            int readByte = fileHandler.readByte();                
	            if( readByte == 0xA ) {
	                if(filePointer == fileLength) {
	                    continue;
	                } else {
	                    break;
	                }
	            } else if(readByte == 0xD) {
	                if(filePointer == fileLength - 1) {
	                    continue;
	                } else {
	                    break;
	                }                    
	            }
	            sb.append((char)readByte);
	        }
	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch(FileNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    } catch(IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	public static String extractField(int position, String regex, String source) {
		String[] splitted = source.split(regex, position + 2);
		return splitted[position];
	}
	
	
	public static double norm(	double value, 
								double minObserved, 
								double maxObserved, 
								double minNorm, 
								double maxNorm
							) {
		double intervalZeroOne = (value - minObserved) / (maxObserved - minObserved);
		return (intervalZeroOne * (maxNorm - minNorm)) + minNorm;
	}
	
	
	public static String readLine(long offset, String filePointer) throws IOException {
		String result;
		RandomAccessFile raf = new RandomAccessFile(filePointer, "r");
		raf.seek(offset);
		// use BufferedReader to read line instead of raf.readLine() for performance reasons:
		BufferedReader reader = new BufferedReader(Channels.newReader(raf.getChannel(), "ISO-8859-1"));
		result = reader.readLine();			
		raf.close();
		return result;
	}

	
}