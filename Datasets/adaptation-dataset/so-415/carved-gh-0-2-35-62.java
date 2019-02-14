public class foo{
	/**
	 * Based on http://stackoverflow.com/questions/27547519/most-efficient-way-to-get-the-last-element-of-a-stream
	 */
	public static <T> Optional<T> findLastOf(Stream<T> stream) {
		Spliterator<T> split = stream.spliterator();

		if (split.hasCharacteristics(Spliterator.SIZED | Spliterator.SUBSIZED)) {
			while (true) {
				Spliterator<T> part = split.trySplit();

				if (part == null) {
					break;
				}

				if (split.getExactSizeIfKnown() == 0) {
					split = part;
					break;
				}
			}
		}

		T value = null;
		for (Iterator<T> it = traverse(split); it.hasNext(); ) {
			value = it.next();
		}

		return Optional.ofNullable(value);
	}
}