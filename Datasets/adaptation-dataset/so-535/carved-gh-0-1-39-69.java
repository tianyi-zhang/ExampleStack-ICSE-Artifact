public class foo{
    /**
     * Compares maps for value-equality:
     * 
     * 1. Check that the maps are the same size
     * 2. Get the set of keys from one map
     * 3. For each key from that set you retrieved, 
     *    check that the value retrieved from each map for that key is the same 
     *    (if the key is absent from one map, that's a total failure of equality).
     * 
     * @param mapA
     * @param mapB
     * @return True if all values from mapA are equal to values from mapB 
     * @see http://stackoverflow.com/questions/2674021/how-to-compare-two-maps-by-their-values
     */
    public static <K,V> boolean compare(Map<K, V> mapA, Map<K, V> mapB) {
        if (mapA == mapB) return true;
        if (mapA == null || mapB == null || mapA.size() != mapB.size()) return false;
        for (K key : mapA.keySet()) {
            V value1 = mapA.get(key);
            V value2 = mapB.get(key);
            if (value1 == null && value2 == null) {
                continue;
            } else if (value1 == null || value2 == null) {
                return false;
            }
            if (!value1.equals(value2)) {
                return false; 
            }
        }
        return true;
    }
}