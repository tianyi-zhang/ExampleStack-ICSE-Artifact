public class foo{
	public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(
			final Map<K, V> map) {
		@SuppressWarnings("unchecked")
		final Map.Entry<K, V>[] array = map.entrySet().toArray(
				new Map.Entry[map.size()]);

		Arrays.sort(array, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(final Map.Entry<K, V> e1,
					final Map.Entry<K, V> e2) {
				return e1.getKey().compareTo(e2.getKey());
			}
		});

		final Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : array) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
}