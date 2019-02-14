public class foo{
    /**
     * MD5 has a string.
     *
     * @param s the string to hash
     * @return the hashed string
     */
    public static String getHashString(final String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage());
            throw new RuntimeException("MD5 message digest not available", e);
        }
    }
}