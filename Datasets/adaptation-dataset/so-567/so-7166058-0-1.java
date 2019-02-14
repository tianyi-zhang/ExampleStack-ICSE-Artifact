public class foo {
/**

 * Sort a map according to values.

 * @param <K> the key of the map.
 * @param <V> the value to sort according to.
 * @param mapToSort the map to sort.

 * @return a map sorted on the values.

 */ 
public static <K, V extends Comparable< ? super V>> Map<K, V>
sortMapByValues(final Map <K, V> mapToSort)
{
    List<Map.Entry<K, V>> entries =
        new ArrayList<Map.Entry<K, V>>(mapToSort.size());  

    entries.addAll(mapToSort.entrySet());

    Collections.sort(entries,
                     new Comparator<Map.Entry<K, V>>()
    {
        @Override
        public int compare(
               final Map.Entry<K, V> entry1,
               final Map.Entry<K, V> entry2)
        {
            return entry1.getValue().compareTo(entry2.getValue());
        }
    });      

    Map<K, V> sortedMap = new LinkedHashMap<K, V>();      

    for (Map.Entry<K, V> entry : entries)
    {
        sortedMap.put(entry.getKey(), entry.getValue());

    }      

    return sortedMap;

}
}