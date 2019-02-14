public class foo{
    public static String decrypt(String key1, String iv1, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(iv1.getBytes(Constants.BASE_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes(Constants.BASE_ENCODING), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));
            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}