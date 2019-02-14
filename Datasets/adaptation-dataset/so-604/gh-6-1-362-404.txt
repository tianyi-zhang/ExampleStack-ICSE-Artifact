//
// Copyright 2010 Cinch Logic Pty Ltd.
//
// http://www.chililog.com
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package org.chililog.server.common;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.NullArgumentException;

/**
 * <p>
 * Utilities methods for hashing and encrypting.
 * </p>
 * <p>
 * NOTE: see here for list of Sun jdk security providers.
 * http://download.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html
 * </p>
 * 
 * @author vibul
 * 
 */
public class CryptoUtils {

    private static final byte[] AES_ENCRYPTION_STRING_SALT = new byte[] { 3, 56, 23, 120, 34, 92 };
    private static final byte[] AES_ENCRYPTION_INTIALIZATION_VECTOR = new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05,
            0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f };

    /**
     * MD5 hash
     * 
     * @param s
     *            string to hash
     * @return MD5 hash as a hex string
     * @throws ChiliLogException
     */
    public static String createMD5Hash(String s) throws ChiliLogException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(s.getBytes("CP1252"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString();
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to MD5 hash: " + ex.getMessage());
        }
    }

    /**
     * <p>
     * From a password, a number of iterations and a salt, returns the corresponding hash. For convenience, the salt is
     * stored within the hash.
     * </p>
     * 
     * <p>
     * This convention is used: <code>base64(hash(plainTextValue + salt)+salt)</code>
     * </p>
     * 
     * @param plainTextValue
     *            String The password to encrypt
     * @param salt
     *            byte[] The salt. If null, one will be created on your behalf.
     * @return String The hash password
     * @throws ChiliLogException
     *             if SHA-512 is not supported or UTF-8 is not a supported encoding algorithm
     */
    public static String createSHA512Hash(String plainTextValue, byte[] salt) throws ChiliLogException {
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            // Salt generation 64 bits long
            salt = new byte[8];
            random.nextBytes(salt);

            return createSHA512Hash(plainTextValue, salt, true);
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to hash passwords. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * From a password, a number of iterations and a salt, returns the corresponding hash.
     * </p>
     * <p>
     * If the salt is to be appended, this convention is used: <code>base64(hash(plainTextValue + salt)+salt)</code>
     * </p>
     * <p>
     * If the salt is NOT to be appended, this convention is used: <code>base64(hash(plainTextValue + salt))</code>
     * </p>
     * 
     * @param plainTextValue
     *            String The password to encrypt
     * @param salt
     *            byte[] The salt. If null, one will be created on your behalf.
     * @param appendSalt
     *            True if the salt is to be appended to hashed value. In this way, for convenience, the salt can be kept
     *            with the hash. Use this only if the hash is to be kept internal to this app. If the hash is to be sent
     *            to external systems, set this to false and store the hash internally.
     * @return String The hash password
     * @throws ChiliLogException
     *             if SHA-512 is not supported or UTF-8 is not a supported encoding algorithm
     */
    public static String createSHA512Hash(String plainTextValue, byte[] salt, boolean appendSalt)
            throws ChiliLogException {
        try {
            if (plainTextValue == null) {
                throw new NullArgumentException("plainTextValue");
            }
            if (salt == null) {
                throw new NullArgumentException("salt");
            }

            // Convert plain text into a byte array.
            byte[] plainTextBytes = plainTextValue.getBytes("UTF-8");

            // Allocate array, which will hold plain text and salt.
            byte[] plainTextWithSaltBytes = new byte[plainTextBytes.length + salt.length];

            // Copy plain text bytes into resulting array.
            for (int i = 0; i < plainTextBytes.length; i++) {
                plainTextWithSaltBytes[i] = plainTextBytes[i];
            }

            // Append salt bytes to the resulting array.
            if (appendSalt) {
                for (int i = 0; i < salt.length; i++) {
                    plainTextWithSaltBytes[plainTextBytes.length + i] = salt[i];
                }
            }

            // Create hash
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            byte[] hashBytes = digest.digest(plainTextWithSaltBytes);

            // Create array which will hold hash and original salt bytes.
            byte[] hashWithSaltBytes = new byte[hashBytes.length + salt.length];

            // Copy hash bytes into resulting array.
            for (int i = 0; i < hashBytes.length; i++) {
                hashWithSaltBytes[i] = hashBytes[i];
            }

            // Append salt bytes to the result.
            for (int i = 0; i < salt.length; i++) {
                hashWithSaltBytes[hashBytes.length + i] = salt[i];
            }

            // Convert hash to string
            Base64 encoder = new Base64(1000, new byte[] {}, false);
            return encoder.encodeToString(hashWithSaltBytes);
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to hash passwords. " + ex.getMessage());
        }
    }

    /**
     * Verifies if a plain text value (like a password) is valid and has not changed. This method assumes that the salt
     * is stored in the hash.
     * 
     * @param plainTextValue
     *            plain text value to check against the hash value
     * @param hashValue
     *            expected has value as returned by <code>createHash</code>.
     * @return true if the plain text value has not been changed, false if not
     * @throws ChiliLogException
     *             if SHA-512 is not supported or UTF-8 is not a supported encoding algorithm
     */
    public static boolean verifyHash(String plainTextValue, String hashValue) throws ChiliLogException {
        return verifyHash(plainTextValue, null, hashValue);
    }

    /**
     * Verifies if a plain text value (like a password) is valid and has not changed.
     * 
     * @param plainTextValue
     *            plain text value to check against the hash value
     * @param salt
     *            salt to add to the hash. If null, this assumes the the salt is stored within the hash.
     * @param hashValue
     *            expected has value as returned by <code>createHash</code>.
     * @return true if the plain text value has not been changed, false if not
     * @throws ChiliLogException
     *             if SHA-512 is not supported or UTF-8 is not a supported encoding algorithm
     */
    public static boolean verifyHash(String plainTextValue, byte[] salt, String hashValue) throws ChiliLogException {
        try {
            if (plainTextValue == null) {
                throw new NullArgumentException("plainTextValue");
            }

            // Convert base64-encoded hash value into a byte array.
            Base64 decoder = new Base64(1000, new byte[] {}, false);
            byte[] hashWithSaltBytes = decoder.decode(hashValue);

            // We must know size of hash (without salt).
            int hashSizeInBits, hashSizeInBytes;

            // Size of hash is based on the specified algorithm - i.e. 512 for SHA-512.
            hashSizeInBits = 512;

            // Convert size of hash from bits to bytes.
            hashSizeInBytes = hashSizeInBits / 8;

            // Make sure that the specified hash value is long enough.
            if (hashWithSaltBytes.length < hashSizeInBytes) {
                return false;
            }

            // Get the salt. If not passed in, then assume salt is stored with the hash
            boolean saltAppended = (salt == null);
            byte[] saltBytes = salt;
            if (saltAppended) {
                // Allocate array to hold original salt bytes retrieved from hash.
                saltBytes = new byte[hashWithSaltBytes.length - hashSizeInBytes];

                // Copy salt from the end of the hash to the new array.
                for (int i = 0; i < saltBytes.length; i++) {
                    saltBytes[i] = hashWithSaltBytes[hashSizeInBytes + i];
                }
            }

            // Compute a new hash string.
            String expectedHashString = createSHA512Hash(plainTextValue, saltBytes, saltAppended);

            // If the computed hash matches the specified hash,
            // the plain text value must be correct.
            return (hashValue.equals(expectedHashString));
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to verify passwords. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Encrypt a plain text string using AES. The output is an encrypted plain text string. See
     * http://stackoverflow.com/questions/992019/java-256bit-aes-encryption/992413#992413
     * </p>
     * <p>
     * The algorithm used is <code>base64(aes(plainText))</code>
     * </p>
     * 
     * 
     * @param plainText
     *            text to encrypt
     * @param password
     *            password to use for encryption
     * @return encrypted text
     * @throws ChiliLogException
     */
    public static String encryptAES(String plainText, String password) throws ChiliLogException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), AES_ENCRYPTION_STRING_SALT, 1024, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            byte[] plainTextBytes = plainText.getBytes("UTF-8");

            AlgorithmParameterSpec paramSpec = new IvParameterSpec(AES_ENCRYPTION_INTIALIZATION_VECTOR);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret, paramSpec);
            byte[] cipherText = cipher.doFinal(plainTextBytes);

            // Convert hash to string
            Base64 encoder = new Base64(1000, new byte[] {}, false);
            return encoder.encodeToString(cipherText);
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to encrypt. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Decrypt an encrypted text string using AES. The output is the plain text string.
     * </p>
     * 
     * @param encryptedText
     *            encrypted text returned by <code>encrypt</code>
     * @param password
     *            password used at the time of encryption
     * @return decrypted plain text string
     * @throws ChiliLogException
     */
    public static String decryptAES(String encryptedText, String password) throws ChiliLogException {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), AES_ENCRYPTION_STRING_SALT, 1024, 128);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            Base64 decoder = new Base64(1000, new byte[] {}, false);
            byte[] encryptedTextBytes = decoder.decode(encryptedText);

            AlgorithmParameterSpec paramSpec = new IvParameterSpec(AES_ENCRYPTION_INTIALIZATION_VECTOR);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, paramSpec);
            byte[] plainTextBytes = cipher.doFinal(encryptedTextBytes);

            return new String(plainTextBytes, "UTF-8");
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to decrpt. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Encrypt a plain text string using TripleDES. The output is an encrypted plain text string. See
     * http://stackoverflow.com/questions/20227/how-do-i-use-3des-encryption-decryption-in-java
     * </p>
     * <p>
     * The algorithm used is <code>base64(tripleDES(plainText))</code>
     * </p>
     * <p>
     * TripleDES is a lot quicker than AES.
     * </p>
     * 
     * @param plainText
     *            text to encrypt
     * @param password
     *            password to use for encryption
     * @return encrypted text
     * @throws ChiliLogException
     */
    public static String encryptTripleDES(String plainText, String password) throws ChiliLogException {
        try {
            return encryptTripleDES(plainText, password.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to encrypt. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Encrypt a plain text string using TripleDES. The output is an encrypted plain text string. See
     * http://stackoverflow.com/questions/20227/how-do-i-use-3des-encryption-decryption-in-java
     * </p>
     * <p>
     * The algorithm used is <code>base64(tripleDES(plainText))</code>
     * </p>
     * <p>
     * TripleDES is a lot quicker than AES.
     * </p>
     * 
     * @param plainText
     *            text to encrypt
     * @param password
     *            password to use for encryption
     * @return encrypted text
     * @throws ChiliLogException
     */
    public static String encryptTripleDES(String plainText, byte[] password) throws ChiliLogException {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest(password);
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);

            final byte[] plainTextBytes = plainText.getBytes("UTF-8");
            final byte[] cipherText = cipher.doFinal(plainTextBytes);

            // Convert hash to string
            Base64 encoder = new Base64(1000, new byte[] {}, false);
            return encoder.encodeToString(cipherText);
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to encrypt. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Decrypt an encrypted text string using TripleDES. The output is the plain text string.
     * </p>
     * 
     * @param encryptedText
     *            encrypted text returned by <code>encrypt</code>
     * @param password
     *            password used at the time of encryption
     * @return decrypted plain text string
     * @throws ChiliLogException
     */
    public static String decryptTripleDES(String encryptedText, String password) throws ChiliLogException {
        try {
            return decryptTripleDES(encryptedText, password.getBytes("UTF-8"));
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to decrpt. " + ex.getMessage());
        }
    }

    /**
     * <p>
     * Decrypt an encrypted text string using TripleDES. The output is the plain text string.
     * </p>
     * 
     * @param encryptedText
     *            encrypted text returned by <code>encrypt</code>
     * @param password
     *            password used at the time of encryption
     * @return decrypted plain text string
     * @throws ChiliLogException
     */
    public static String decryptTripleDES(String encryptedText, byte[] password) throws ChiliLogException {
        try {
            final MessageDigest md = MessageDigest.getInstance("md5");
            final byte[] digestOfPassword = md.digest(password);
            final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            for (int j = 0, k = 16; j < 8;) {
                keyBytes[k++] = keyBytes[j++];
            }

            Base64 decoder = new Base64(1000, new byte[] {}, false);
            byte[] encryptedTextBytes = decoder.decode(encryptedText);

            final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
            final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
            final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            decipher.init(Cipher.DECRYPT_MODE, key, iv);
            final byte[] plainTextBytes = decipher.doFinal(encryptedTextBytes);

            return new String(plainTextBytes, "UTF-8");
        } catch (Exception ex) {
            throw new ChiliLogException(ex, "Error attempting to decrpt. " + ex.getMessage());
        }
    }
}
