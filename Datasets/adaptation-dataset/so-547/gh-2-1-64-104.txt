package agent;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringEscapeUtils;

public class Utils {

    public static String deFux(String v) throws Exception {
        // there is a lot of crap in this database. nuke some of it, we cannot search it anyway
        byte[] b = v.getBytes("ISO-8859-1");
        boolean changed = false;
        for (int i = 0; i < b.length; i++) {
            // remove useless control chars
            if ((b[i] >= 0 && b[i] < 32) && b[i] != 13  && b[i] != 10  && b[i] != 9) {
                b[i] = 32;
                changed = true;
            }
            // ^ - we reserve this char for the COPY delimiter
            // " - mangles quoting
            // \ - mangles quoting
            if (b[i] == '^' || b[i] == '"' || b[i] == '\\') {
                b[i] = 32;
                changed = true;
            }
        }
        if (changed) {
            v = new String(b, "ISO-8859-1");
        }
        v = Utils.fixEncoding(v);
        return v;
    }

    public static String deFuxMore(String v) throws Exception {
        byte[] b = v.getBytes("ISO-8859-1");
        boolean changed = false;
        for (int i = 0; i < b.length; i++) {
            // we won't tolerate control chars now, at all
            if (b[i] >= 0 && b[i] < 32) {
                b[i] = 32;
                changed = true;
            }
        }
        if (changed) {
            v = new String(b, "ISO-8859-1");
        }
        return v;
    }

    // http://stackoverflow.com/questions/887148/how-to-determine-if-a-string-contains-invalid-encoded-characters
    public static String fixEncoding(String latin1) {
        try {
            byte[] bytes = latin1.getBytes("ISO-8859-1");
            if (!Utils.validUTF8(bytes))
                return latin1;   
            return new String(bytes, "UTF-8");  
        } catch (UnsupportedEncodingException e) {
            // Impossible, throw unchecked
            throw new IllegalStateException("No Latin1 or UTF-8: " + e.getMessage());
        }
     }

    private static boolean validUTF8(byte[] input) {
        int i = 0;
        // Check for BOM
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int octet = input[i];
            if ((octet & 0x80) == 0) {
                continue; // ASCII
            }

            // Check for UTF-8 leading byte
            if ((octet & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((octet & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((octet & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                // Java only supports BMP so 3 is max
                return false;
            }

            while (i < end) {
                i++;
                if (i >= input.length) {
                    // out of bounds
                    return false;
                }
                octet = input[i];
                if ((octet & 0xC0) != 0x80) {
                    // Not a valid trailing byte
                    return false;
                }
            }
        }
        return true;
    }

}
