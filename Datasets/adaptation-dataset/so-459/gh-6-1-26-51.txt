/*
 * Unless explicitly acquired and licensed from Licensor under another license, the contents of
 * this file are subject to the Reciprocal Public License ("RPL") Version 1.5, or subsequent
 * versions as allowed by the RPL, and You may not copy or use this file in either source code
 * or executable form, except in compliance with the terms and conditions of the RPL
 *
 * All software distributed under the RPL is provided strictly on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND LICENSOR HEREBY DISCLAIMS ALL SUCH
 * WARRANTIES, INCLUDING WITHOUT LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the RPL for specific language
 * governing rights and limitations under the RPL.
 *
 * http://opensource.org/licenses/RPL-1.5
 *
 * Copyright 2012-2015 Open Justice Broker Consortium
 */
package org.ojbc.util.helper;

import org.apache.commons.lang.StringUtils;

/**
 * See here: http://stackoverflow.com/questions/415953/how-can-i-generate-an-md5-hash
 * 
 */
public class Hash {
    /**
     * 
     * @param txt, text in plain format
     * @param hashType MD5,SHA-256 OR SHA1
     * @return hash in hashType 
     */
    public static String getHash(String txt, String salt, String hashType) {
        try {
                    java.security.MessageDigest md = java.security.MessageDigest.getInstance(hashType);
                    
                    if (StringUtils.isNotBlank(salt))
                    {
                    	txt += salt;
                    }	
                    
                    byte[] array = md.digest(txt.getBytes());
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < array.length; ++i) {
                        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
                 }
                    return sb.toString();
            } catch (java.security.NoSuchAlgorithmException e) {
                //error action
            }
            return null;
    }

    public static String md5(String txt) {
        return Hash.getHash(txt,"", "MD5");
    }

    public static String sha1(String txt) {
        return Hash.getHash(txt, "","SHA1");
    }

    public static String sha256(String txt) {
        return Hash.getHash(txt, "","SHA-256");
    }
    
    public static String md5WithSalt(String txt, String salt) {
        return Hash.getHash(txt,salt, "MD5");
    }

    public static String sha1WithSalt(String txt, String salt) {
        return Hash.getHash(txt, salt,"SHA1");
    }

    public static String sha256WithSalt(String txt, String salt) {
        return Hash.getHash(txt, salt,"SHA-256");
    }

    
}