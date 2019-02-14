public class foo{
    public static String decrypt(byte[] headerSaltAndCipherText, String password) {
        try {
            // --- extract salt & encrypted ---

            // header is "Salted__", ASCII encoded, if salt is being used (the
            // default)
            byte[] salt = copyOfRange(headerSaltAndCipherText, SALT_OFFSET, SALT_OFFSET + SALT_SIZE);
            byte[] encrypted = copyOfRange(headerSaltAndCipherText, CIPHERTEXT_OFFSET,
                    headerSaltAndCipherText.length);

            // --- specify cipher and digest for EVP_BytesToKey method ---

            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            // --- create key and IV ---

            // the IV is useless, OpenSSL might as well have use zero's
            final byte[][] keyAndIV = EVP_BytesToKey(KEY_SIZE_BITS / Byte.SIZE,
                    aesCBC.getBlockSize(), md5, salt, password.getBytes("ASCII"), ITERATIONS);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[INDEX_KEY], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[INDEX_IV]);

            // --- initialize cipher instance and decrypt ---

            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decrypted = aesCBC.doFinal(encrypted);

            return new String(decrypted, "ASCII");
        } catch (BadPaddingException e) {
            // AKA "something went wrong"
            throw new IllegalStateException(
                    "Bad password, algorithm, mode or padding;"
                            + " no salt, wrong number of iterations or corrupted ciphertext.");
        } catch (IllegalBlockSizeException e) {
            throw new IllegalStateException(
                    "Bad algorithm, mode or corrupted (resized) ciphertext.");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}