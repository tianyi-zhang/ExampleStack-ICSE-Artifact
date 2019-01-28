import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * In this project you will experiment with a padding oracle attack against a toy web site hosted at
 * crypto-class.appspot.com. Padding oracle vulnerabilities affect a wide variety of products, including secure tokens.
 * This project will show how they can be exploited. We discussed CBC padding oracle attacks in Lecture 7.6, but if you
 * want to read more about them, please see Vaudenay's paper.
 * <p/>
 * Now to business. Suppose an attacker wishes to steal secret information from our target web site
 * crypto-class.appspot.com. The attacker suspects that the web site embeds encrypted customer data in URL parameters
 * such as this:
 * <p/>
 * http://crypto-class.appspot.com/po?er=f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4
 * <p/>
 * That is, when customer Alice interacts with the site, the site embeds a URL like this in web pages it sends to Alice.
 * The attacker intercepts the URL listed above and guesses that the ciphertext following the "po?er=" is a hex encoded
 * AES CBC encryption with a random IV of some secret data about Alice's session.
 * <p/>
 * After some experimentation the attacker discovers that the web site is vulnerable to a CBC padding oracle attack. In
 * particular, when a decrypted CBC ciphertext ends in an invalid pad the web server returns a 403 error code (forbidden
 * request). When the CBC padding is valid, but the message is malformed, the web server returns a 404 error code (URL not found).
 * <p/>
 * Armed with this information your goal is to decrypt the ciphertext listed above. To do so you can send arbitrary HTTP
 * requests to the web site of the form http://crypto-class.appspot.com/po?er="your ciphertext here" and observe the
 * resulting error code. The padding oracle will let you decrypt the given ciphertext one byte at a time. To decrypt a
 * single byte you will need to send up to 256 HTTP requests to the site. Keep in mind that the first ciphertext block
 * is the random IV. The decrypted message is ASCII encoded.
 * <p/>
 * To get you started here is a short Python script that sends a ciphertext supplied on the command line to the site and
 * prints the resulting error code. You can extend this script (or write one from scratch) to implement the padding
 * oracle attack. Once you decrypt the given ciphertext, please enter the decrypted message in the box below.
 * <p/>
 * This project shows that when using encryption you must prevent padding oracle attacks by either using encrypt-then-MAC
 * as in EAX or GCM, or if you must use MAC-then-encrypt then ensure that the site treats padding errors the same way it
 * treats MAC errors.
 */
public class Lesson04 {
    final protected static char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    public static final int BLOCK_SIZE = 16;
    public static final String CIPHER_TEXT = "f20bdba6ff29eed7b046d1df9fb7000058b1ffb4210a580f748b4ac714c001bd4a61044426fb515dad3f21f18aa577c0bdf302936266926ff37dbf7035d5eeb4";
    public static final String ATTACK_URL = "http://crypto-class.appspot.com/po?er=";

    public static void main(String[] args) throws IOException {
        // cipher text
        byte[] c = hexStringToByteArray(CIPHER_TEXT);
        // decrypted text
        String d = "";

        for (int block = 0; block < (c.length - BLOCK_SIZE) / BLOCK_SIZE; block++) {
            // copy current block and all previous
            byte[] cb = Arrays.copyOfRange(c, 0, c.length - block * BLOCK_SIZE);

            for (int i = 0; i < BLOCK_SIZE; i++) {

                byte[] cm = cb.clone();
                byte found = 0;

                // position of byte that will be guessed
                int pos = cb.length - 1 - BLOCK_SIZE - i;

                // try to guess value of this byte
                for (int b = 0; b < 256; b++) {

                    // pad all bytes from current to the end of the block
                    for (int k = 0; k < i + 1; k++) {
                        cm[pos + k] = (byte) (cb[pos + k] ^ (i + 1));
                    }

                    // xor byte with guessed value
                    cm[pos] = (byte) (cm[pos] ^ b);

                    int status = getUrlStatus(ATTACK_URL + byteArrayToHexString(cm));

                    // Log request
                    System.out.println(block + ":" + i + ":" + pos + ":" + b + "> " + status + " = " + byteArrayToHexString(cm));

                    if (status == 404) {
                        // pad is correct
                        found = (byte) b;
                        break;
                    } else if (status == 200) {
                        // pad is correct or no changes to guessed value are made
                        // this can happen if pad equals guessed value, then
                        // pad ^ guessed = 0
                        // save this value as possible result
                        // but wait if 404 is returned
                        found = (byte) b;
                    }
                }

                // save found value for next round
                cb[pos] = (byte) (cb[pos] ^ found);

                // save to decrypted value
                d = (char) found + d;

                // Log decrypted value
                System.out.println("found >> " + found + " = " + d);
                System.out.println();
            }
        }
    }

    public static int getUrlStatus(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        URLConnection connection = url.openConnection();

        connection.connect();

        if (connection instanceof HttpURLConnection) {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;

            return httpConnection.getResponseCode();
        } else {
            return 0;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), BLOCK_SIZE) << 4)
                    + Character.digit(s.charAt(i + 1), BLOCK_SIZE));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
