public class foo {
static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
        new Comparator<Map.Entry<K,V>>() {
            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                if (e1.getKey().equals(e2.getKey())) {
                    return res; // Code will now handle equality properly
                } else {
                    return res != 0 ? res : 1; // While still adding all entries
                }
            }
        }
    );
    sortedEntries.addAll(map.entrySet());
    return sortedEntries;
}
}