public class foo {
    /**
     * Converts time to a human readable format within the specified range
     *
     * @param duration the time in milliseconds to be converted
     * @param max      the highest time unit of interest
     * @param min      the lowest time unit of interest
     */
    public static String formatMillis(long duration, TimeUnit max, TimeUnit min) {
        StringBuilder res = new StringBuilder();

        TimeUnit current = max;

        while (duration > 0) {
            long temp = current.convert(duration, MILLISECONDS);

            if (temp > 0) {
                duration -= current.toMillis(temp);
                res.append(temp).append(" ").append(current.name().toLowerCase());
                if (temp < 2) res.deleteCharAt(res.length() - 1);
                res.append(", ");
            }

            if (current == min) break;

            current = TimeUnit.values()[current.ordinal() - 1];
        }

        // clean up our formatting....

        // we never got a hit, the time is lower than we care about
        if (res.lastIndexOf(", ") < 0) return "0 " + min.name().toLowerCase();

        // yank trailing  ", "
        res.deleteCharAt(res.length() - 2);

        //  convert last ", " to " and"
        int i = res.lastIndexOf(", ");
        if (i > 0) {
            res.deleteCharAt(i);
            res.insert(i, " and");
        }

        return res.toString();
    }
}