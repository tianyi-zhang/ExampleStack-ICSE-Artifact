package edu.cmu.cs.dickerson.kpd.helper;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil {


	/**
	 * Sorts a map based on value, returns sorted map with predictable iteration
	 * Initial code from: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	 * @param map Map with assumed-Compable values
	 * @param doReverse True if comparator's ordering should be reversed, False if normal
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map, boolean doReverse )
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );

		Comparator<Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>()
				{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
				return (o1.getValue()).compareTo( o2.getValue() );
			}
				};
		
		if(doReverse) {
			Collections.sort( list, Collections.reverseOrder(comparator));
		} else {
			Collections.sort( list, comparator);
		}
		
		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	/**
	 * Sorts a map based on value, returns sorted map with predictable iteration
	 * Initial code from: http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
	 * @param map Map with assumed-Compable values
	 * @return
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map) {
		return sortByValue(map, false);
	}

}
