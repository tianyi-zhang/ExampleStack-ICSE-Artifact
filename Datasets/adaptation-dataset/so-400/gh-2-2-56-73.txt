package com.tipz.app.control.util;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * This is a simple encrypt/decrypt way. It's not 100% secured but gives some security
 * Idea from : http://stackoverflow.com/questions/785973/what-is-the-most-appropriate-way-to-store-user-settings-in-android-application
 */
public class BasicStringSecurity implements SecuredPreferenceUtil.SecureStringHandler{

    public static final String UTF8 = "utf-8";
    private final char[] mSerkit;

    /***
     * @param serkit The key used when encrypting/decrypting. You will need to reuse this key when encrypting and decrypting
     *               so make sure you init this value with a persistent value between app initializations, otherwise you will
     *               lose track of the real values you have used
     *
     */
    public BasicStringSecurity(char[] serkit) {
        mSerkit = serkit;
    }

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
