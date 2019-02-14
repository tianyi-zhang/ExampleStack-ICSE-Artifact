public class foo{
    /*
    * Levenshtein Edit Distance
    * http://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
    */
    private static int editDistance(String s1, String s2) {
        String s1LowerCase = s1.toLowerCase();
        String s2LowerCase = s2.toLowerCase();

        int[] costs = new int[s2LowerCase.length() + 1];
        for (int i = 0; i <= s1LowerCase.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2LowerCase.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else if (j > 0) {
                    int newValue = costs[j - 1];
                    if (s1LowerCase.charAt(i - 1) != s2LowerCase.charAt(j - 1)) {
                        newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                    }
                    costs[j - 1] = lastValue;
                    lastValue = newValue;

                }
            }
            if (i > 0) {
                costs[s2LowerCase.length()] = lastValue;
            }
        }
        LOGGER.debug("String 1: " + s1LowerCase + " String 2: " + s2LowerCase + " Distance: " + costs[s2LowerCase.length()]);
        return costs[s2LowerCase.length()];
    }
}