public class foo{
    // http://stackoverflow.com/a/22947052
    public static <T> List<List<T>> getPages(Collection<T> c, int pageSize) {
        if (c == null) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<>(c);

        if ((pageSize <= 0) || (pageSize > list.size())) {
            pageSize = list.size();
        }

        int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);

        List<List<T>> pages = new ArrayList<>(numPages);

        for (int pageNum = 0; pageNum < numPages; ) {
            pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
        }

        return pages;
    }
}