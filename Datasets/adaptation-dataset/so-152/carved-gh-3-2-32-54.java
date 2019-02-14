public class foo{
    /**
     * Use this encryption when the values should be used only inside the scope of the application, since it depends on the device.
     * For example - use when a value needs to be scrambled before it's saved to the preferences, and decrypted when read from there.
     */
    public String encrypt(Context context, String value) {
        if (value == null){
            return null;
        }

        try {
            final byte[] bytes = value.getBytes(UTF8);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            SecretKey key = keyFactory.generateSecret(new PBEKeySpec(mSerkit));
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(
                    Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).getBytes(UTF8), 20));
            return new String(Base64.encode(pbeCipher.doFinal(bytes), Base64.NO_WRAP), UTF8);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}