public class foo {
    public static void main(String[] args) {
        try {
            // --- read base 64 encoded file ---

            File f = new File(args[ARG_INDEX_FILENAME]);
            List<String> lines = Files.readAllLines(f.toPath(), ASCII);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line.trim());
            }
            String dataBase64 = sb.toString();
            byte[] headerSaltAndCipherText = Base64.decode(dataBase64);

            // --- extract salt & encrypted ---

            // header is "Salted__", ASCII encoded, if salt is being used (the default)
            byte[] salt = Arrays.copyOfRange(
                    headerSaltAndCipherText, SALT_OFFSET, SALT_OFFSET + SALT_SIZE);
            byte[] encrypted = Arrays.copyOfRange(
                    headerSaltAndCipherText, CIPHERTEXT_OFFSET, headerSaltAndCipherText.length);

            // --- specify cipher and digest for EVP_BytesToKey method ---

            Cipher aesCBC = Cipher.getInstance("AES/CBC/PKCS5Padding");
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            // --- create key and IV  ---

            // the IV is useless, OpenSSL might as well have use zero's
            final byte[][] keyAndIV = EVP_BytesToKey(
                    KEY_SIZE_BITS / Byte.SIZE,
                    aesCBC.getBlockSize(),
                    md5,
                    salt,
                    args[ARG_INDEX_PASSWORD].getBytes(ASCII),
                    ITERATIONS);
            SecretKeySpec key = new SecretKeySpec(keyAndIV[INDEX_KEY], "AES");
            IvParameterSpec iv = new IvParameterSpec(keyAndIV[INDEX_IV]);

            // --- initialize cipher instance and decrypt ---

            aesCBC.init(Cipher.DECRYPT_MODE, key, iv);
            byte[] decrypted = aesCBC.doFinal(encrypted);

            String answer = new String(decrypted, ASCII);
            System.out.println(answer);
        } catch (BadPaddingException e) {
            // AKA "something went wrong"
            throw new IllegalStateException(
                    "Bad password, algorithm, mode or padding;" +
                    " no salt, wrong number of iterations or corrupted ciphertext.");
        } catch (IllegalBlockSizeException e) {
            throw new IllegalStateException(
                    "Bad algorithm, mode or corrupted (resized) ciphertext.");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}