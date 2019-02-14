public class foo{
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
}