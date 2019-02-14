/*
 * Copyright © 2016 TIBCO Software,Inc.All rights reserved.
 * http://community.jaspersoft.com/project/jaspermobile-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile for Android.
 *
 * TIBCO Jaspersoft Mobile is free software:you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation,either version 3of the License,or
 * (at your option)any later version.
 *
 * TIBCO Jaspersoft Mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile for Android.If not,see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.jaspermobile.network.cookie;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.HttpCookie;

/**
 * Based on the code from this stackoverflow answer <a href="http://stackoverflow.com/a/25462286/980387">http://stackoverflow.com/a/25462286/980387</a> by janoliver
 * Modifications in the structure of the class and addition of serialization of httpOnly attribute
 */
final class SerializableHttpCookie implements Serializable {
    private static final String TAG = SerializableHttpCookie.class
            .getSimpleName();

    private static final long serialVersionUID = 6374381323722046732L;

    private transient HttpCookie cookie;

    // Workaround httpOnly: The httpOnly attribute is not accessible so when we
    // serialize and deserialize the cookie it not preserve the same value. We
    // need to access it using reflection
    private Field fieldHttpOnly;

    public SerializableHttpCookie() {
    }

    public String encode(HttpCookie cookie) {
        this.cookie = cookie;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(this);
        } catch (IOException e) {
            Log.d(TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    public HttpCookie decode(String encodedCookie) {
        byte[] bytes = hexStringToByteArray(encodedCookie);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                bytes);
        HttpCookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject()).cookie;
        } catch (IOException e) {
            Log.d(TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "ClassNotFoundException in decodeCookie", e);
        }

        return cookie;
    }

    // Workaround httpOnly (getter)
    private boolean getHttpOnly() {
        try {
            initFieldHttpOnly();
            return (boolean) fieldHttpOnly.get(cookie);
        } catch (Exception e) {
            // NoSuchFieldException || IllegalAccessException ||
            // IllegalArgumentException
            Log.w(TAG, e);
        }
        return false;
    }

    // Workaround httpOnly (setter)
    private void setHttpOnly(boolean httpOnly) {
        try {
            initFieldHttpOnly();
            fieldHttpOnly.set(cookie, httpOnly);
        } catch (Exception e) {
            // NoSuchFieldException || IllegalAccessException ||
            // IllegalArgumentException
            Log.w(TAG, e);
        }
    }

    private void initFieldHttpOnly() throws NoSuchFieldException {
        fieldHttpOnly = cookie.getClass().getDeclaredField("httpOnly");
        fieldHttpOnly.setAccessible(true);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(cookie.getName());
        out.writeObject(cookie.getValue());
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getCommentURL());
        out.writeObject(cookie.getDomain());
        out.writeLong(cookie.getMaxAge());
        out.writeObject(cookie.getPath());
        out.writeObject(cookie.getPortlist());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.getSecure());
        out.writeBoolean(cookie.getDiscard());
        out.writeBoolean(getHttpOnly());
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        cookie = new HttpCookie(name, value);
        cookie.setComment((String) in.readObject());
        cookie.setCommentURL((String) in.readObject());
        cookie.setDomain((String) in.readObject());
        cookie.setMaxAge(in.readLong());
        cookie.setPath((String) in.readObject());
        cookie.setPortlist((String) in.readObject());
        cookie.setVersion(in.readInt());
        cookie.setSecure(in.readBoolean());
        cookie.setDiscard(in.readBoolean());
        setHttpOnly(in.readBoolean());
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't
     * have to rely on any large Base64 libraries. Can be overridden if you
     * like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }

    /**
     * Converts hex values from strings to byte array
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    private byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}
