public class foo{
    /**
     * Compare two version numbers with the format 1, 1.1, or 1.1.1
     * 
     * From http://stackoverflow.com/questions/6701948/efficient-way-to-compare-version-strings-in-java
     * 
     * @param newer The version string considered to be newer
     * @param older The version string considered to be older
     * @return -1 if newer is less than older <br/>
     *         0 if newer is equal to older <br/>
     *         1 if newer is greater than older
     */
    public static int compareVersionNumbers(String newer, String older) {
        if(!isSetAfterTrim(newer)) {
            newer = "0"; //$NON-NLS-1$
        }
        if(!isSetAfterTrim(older)) {
            older = "0"; //$NON-NLS-1$
        }
        
        String[] vals1 = newer.split("\\."); //$NON-NLS-1$
        String[] vals2 = older.split("\\."); //$NON-NLS-1$
        
        int i = 0;
        while(i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
            i++;
        }

        if(i < vals1.length && i < vals2.length) {
            int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
            return Integer.signum(diff);
        }

        return Integer.signum(vals1.length - vals2.length);
    }
}