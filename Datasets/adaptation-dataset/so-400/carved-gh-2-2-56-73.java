public class foo{
    public String decrypt(Context context, String value) {
        if (value == null){
            return null;
        }

        try {
            final byte[] bytes = Base64.decode(value, Base64.DEFAULT);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(
                    Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID).getBytes(UTF8), 20));
            return new String(pbeCipher.doFinal(bytes),UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}