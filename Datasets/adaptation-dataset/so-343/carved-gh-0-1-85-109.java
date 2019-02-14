public class foo{
    // http://stackoverflow.com/a/12297231/4327834
    // #efficiency
    public static String[] stringSplitter(String s, int interval) {
        if ((s == null) || (s.isEmpty())) {
            return new String[]{};
        }

        if (interval == 0) {
            return new String[]{s};
        }

        int arrayLength = (int) Math.ceil((((double) s.length() / (double) interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }

        result[lastIndex] = s.substring(j);

        return result;
    }
}