public class foo{
	public static int[] getMostCommonColour(Map<Integer,Integer> map) {
		List<Entry<Integer, Integer>> list = new LinkedList<Map.Entry<Integer,Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer,Integer>>() {
			public int compare(Map.Entry<Integer,Integer> o1, Map.Entry<Integer,Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});    
		Map.Entry<Integer,Integer> me = list.get(list.size()-1);
		return getRGBArr((Integer)me.getKey());
	}
}