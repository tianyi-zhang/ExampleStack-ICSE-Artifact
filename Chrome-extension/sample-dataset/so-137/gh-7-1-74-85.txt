package com.manniwood.pgtypes;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;

import org.postgresql.util.GT;
import org.postgresql.util.PGBinaryObject;
import org.postgresql.util.PGobject;
import org.postgresql.util.PSQLException;
import org.postgresql.util.PSQLState;

public class MD5Hash extends PGobject implements PGBinaryObject,
        Serializable, Cloneable {

    public static final String TYPE_NAME = "md5hash";  // presumably must be exact same name as datatype
    private static final int MD5_BYTE_LEN = 16;
    private static final int MD5_STR_LEN = 32;
    private static final char[] HEX_CHAR_MAP = "0123456789abcdef".toCharArray();

    private static final long serialVersionUID = 1L;

    private byte[] md5;

    /**
     * Required constructor
     */
    public MD5Hash() {
        setType(TYPE_NAME);
    }

    /**
     * Convenience constructor for clone method
     * @param md5
     */
    public MD5Hash(byte[] md5) {
        setType(TYPE_NAME);
        this.md5 = md5;
    }

    /**
     * Convenience constructor for when you have a string representation of an md5sum
     * @param str
     * @throws SQLException
     */
    public MD5Hash(String str) throws SQLException {
        setType(TYPE_NAME);
        setValue(str);
    }

    @Override
    public void setValue(String value) throws SQLException {
        md5 = hexStringToByteArray(value);
    }

    public byte[] hexStringToByteArray(String s) throws SQLException {
        if (s.length() != MD5_STR_LEN) {
            throw new PSQLException(GT.tr("Conversion to type {0} failed: {1}.", new Object[]{getType(),"Wrong length."}), PSQLState.DATA_TYPE_MISMATCH);
        }
        int strLen = s.length();
        byte[] data = new byte[MD5_BYTE_LEN];
        for (int i = 0; i < strLen; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Override
    public String getValue() {
        return byteArrayToHexString(md5);
    }

    private String byteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        char[] hexChars = new char[MD5_STR_LEN];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_CHAR_MAP[v >>> 4];
            hexChars[j * 2 + 1] = HEX_CHAR_MAP[v & 0x0F];
        }
        return new String(hexChars);
    }

    @Override
    public String toString() {
        return getValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof MD5Hash)) {
            return false;
        }
        MD5Hash other = (MD5Hash) obj;
        if (md5 == null && other.md5 == null) {
            return true;
        }
        if (md5 == null || other.md5 == null) {
            return false;
        }
        return Arrays.equals(md5, other.md5);
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return new MD5Hash(Arrays.copyOf(md5, MD5_BYTE_LEN));
    }


    ///////////////////// binary
    @Override
    public void setByteValue(byte[] value, int offset) throws SQLException {
        if (value.length - offset < MD5_BYTE_LEN) {
            throw new PSQLException(GT.tr("Conversion to type {0} failed: {1}.", new Object[]{getType(),"Wrong length."}), PSQLState.DATA_TYPE_MISMATCH);
        }
        md5 = Arrays.copyOf(value, offset);
    }

    @Override
    public int lengthInBytes() {
        return MD5_BYTE_LEN;
    }

    @Override
    public void toBytes(byte[] bytes, int offset) {
        bytes = Arrays.copyOf(md5, MD5_BYTE_LEN);
    }

}
