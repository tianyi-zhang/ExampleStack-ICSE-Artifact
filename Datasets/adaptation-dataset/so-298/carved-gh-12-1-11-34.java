public class foo{
	/**
	 * Sorts a Map by value
	 * 
	 * Partially buggy due to
	 * http://stackoverflow.com/questions/109383/how-to-sort
	 * -a-mapkey-value-on-the-values-in-java/1283722#1283722
	 * 
	 * @param map
	 * @return SortedMap by Value
	 */
	@Deprecated
	public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortByValue(
			final Map<K, V> map) {
		final List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> op1,
					final Map.Entry<K, V> op2) {
				return (op2.getValue()).compareTo(op1.getValue());
			}
		});
		return list;
	}
}