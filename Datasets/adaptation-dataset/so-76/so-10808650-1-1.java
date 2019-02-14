public class foo {
public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) 
{
    @SuppressWarnings("unchecked")
    Map.Entry<K,V>[] array = map.entrySet().toArray(new Map.Entry[map.size()]);

    Arrays.sort(array, new Comparator<Map.Entry<K, V>>() 
    {
        public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) 
        {
            return e1.getValue().compareTo(e2.getValue());
        }
    });

    Map<K, V> result = new LinkedHashMap<K, V>();
    for (Map.Entry<K, V> entry : array)
        result.put(entry.getKey(), entry.getValue());

    return result;
}
}