public class foo {
public static <K, V> boolean mapEquals(Map<K, V> leftMap, Map<K, V> rightMap) {
    if (leftMap == rightMap) return true;
    if (leftMap == null || rightMap == null || leftMap.size() != rightMap.size()) return false;
    for (K key : leftMap.keySet()) {
        V value1 = leftMap.get(key);
        V value2 = rightMap.get(key);
        if (value1 == null && value2 == null)
            continue;
        else if (value1 == null || value2 == null)
            return false;
        if (!value1.equals(value2))
            return false;
    }
    return true;
}
}