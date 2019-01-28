package com.codeandstrings.niohttp.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class FileUtils {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static final String computeEtag(String filename, Date lastModifiedDate) {

        StringBuilder sequence = new StringBuilder();

        sequence.append(filename);
        sequence.append("_");
        sequence.append(lastModifiedDate.getTime());

        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte sequenceBytes[] = sequence.toString().getBytes("UTF-8");
            byte hashBytes[] = digest.digest(sequenceBytes);

            return bytesToHex(hashBytes).toLowerCase();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
