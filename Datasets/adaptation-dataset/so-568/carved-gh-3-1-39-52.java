public class foo{
	public static String encrypt(String key1, String iv1, String value) {
		try {
			IvParameterSpec iv = new IvParameterSpec(iv1.getBytes(Constants.BASE_ENCODING));
            SecretKeySpec skeySpec = new SecretKeySpec(key1.getBytes(Constants.BASE_ENCODING), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            //System.out.println("encrypted string:" + Base64.encodeBase64String(encrypted));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}