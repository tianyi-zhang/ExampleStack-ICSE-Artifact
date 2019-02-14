public class foo{
	public final int getSectionForPosition(int position) {
		int section = 0;

		for (ListAdapter piece : pieces) {
			int size = piece.getCount();

			if (position < size) {
				if (piece instanceof SectionIndexer) {
					return (section + ((SectionIndexer) piece).getSectionForPosition(position));
				}

				return (0);
			} else {
				if (piece instanceof SectionIndexer) {
					Object[] sections = ((SectionIndexer) piece).getSections();

					if (sections != null) {
						section += sections.length;
					}
				}
			}

			position -= size;
		}

		return (0);
	}
}