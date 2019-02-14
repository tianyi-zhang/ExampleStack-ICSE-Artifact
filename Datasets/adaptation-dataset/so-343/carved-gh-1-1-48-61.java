public class foo{
    // Source : http://stackoverflow.com/questions/12295711/split-a-string-at-every-nth-position
    private static String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++, j += interval) {
            result[i] = s.substring(j, j + interval);
        } //Add the last bit
        result[lastIndex] = s.substring(j);

        return result;
    }
}