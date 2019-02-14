public class foo{
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
}