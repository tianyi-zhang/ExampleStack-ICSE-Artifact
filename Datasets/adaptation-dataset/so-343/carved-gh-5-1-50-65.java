public class foo{
    private static String[] splitStringEvery(String s, int interval) {         // based on: http://stackoverflow.com/questions/12295711/split-a-string-at-every-nth-position
        int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        }
        if (lastIndex >= 0) {
            result[lastIndex] = s.substring(j);
        }

        return result;
    }
}