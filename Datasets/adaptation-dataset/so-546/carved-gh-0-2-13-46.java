public class foo{
	/**
	 * Splits a list into smaller sublists.
	 * The original list remains unmodified and changes on the sublists are not propagated to the original list.
	 *
	 * @param original           The list to split
	 * @param maxListSize        The max amount of element a sublist can hold.
	 * @param listImplementation The implementation of List to be used to create the returned sublists
	 * @return A list of sublists
	 * @throws IllegalArgumentException if the argument maxListSize is zero or a negative number
	 * @throws NullPointerException     if arguments original or listImplementation are null
	 */
	public static final <T> List<List<T>> split(final List<T> original, final int maxListSize,
	                                            final Class<? extends List> listImplementation)
	{
		if (maxListSize <= 0)
		{
			throw new IllegalArgumentException("maxListSize must be greater than zero");
		}

		final T[] elements = (T[]) original.toArray();
		final int maxChunks = (int) Math.ceil(elements.length / (double) maxListSize);

		final List<List<T>> lists = new ArrayList<List<T>>(maxChunks);
		for (int i = 0; i < maxChunks; i++)
		{
			final int from = i * maxListSize;
			final int to = Math.min(from + maxListSize, elements.length);
			final T[] range = Arrays.copyOfRange(elements, from, to);

			lists.add(createSublist(range, listImplementation));
		}

		return lists;
	}
}